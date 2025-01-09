package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.handler.exception.BudgetNotFoundException;
import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.budget.BudgetMapper;
import com.andwis.travel_with_anna.trip.budget.BudgetService;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.expanse.*;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {
    private static final String USER_UNAUTHORIZED = "You are not authorized to access";
    private static final String TRIP_NOT_FOUND_MESSAGE = "Trip not found when generating expense PDF report";
    private static final String BUDGET_NOT_FOUND_MESSAGE = "Budget cannot be null when generating expense PDF report";

    private final TripReportCreator tripReportParagraphCreator;
    private final ExpanseReportCreator expanseReportParagraphCreator;
    private final TripService tripService;
    private final ExpanseService expanseService;
    private final UserAuthenticationService userAuthenticationService;
    private final ExpanseSummaryReportCreator expanseSummaryReportCreator;
    private final BudgetService budgetService;

    @Transactional
    public ByteArrayOutputStream createTripPdfReport(Long tripId, UserDetails connectedUser) {
        try {
            ByteArrayOutputStream tripPdfReport = new ByteArrayOutputStream();
            Trip trip = authenticateAndFetchTrip(tripId, connectedUser);
            List<Day> days = trip.getDaysInOrder();

            Document document = initializePdfDocument(tripPdfReport);
            document.add(tripReportParagraphCreator.getTitle(trip.getTripName()));
            document.add(tripReportParagraphCreator.getDates(
                    days.getFirst().getDate().toString(),
                    days.getLast().getDate().toString()
            ));

            for (Day day : days) {
                processDay(document, day);
            }

            document.close();
            return tripPdfReport;
        } catch (Exception e) {
            handleException(e);
            return new ByteArrayOutputStream();
        }
    }

    @Transactional
    public ByteArrayOutputStream createExpansePdfReport(Long tripId, UserDetails connectedUser) {
        try {
            ByteArrayOutputStream tripPdfReport = new ByteArrayOutputStream();
            Trip trip = authenticateAndFetchTrip(tripId, connectedUser);

            Budget budget = trip.getBudget();
            if (budget == null) {
                throw new BudgetNotFoundException(BUDGET_NOT_FOUND_MESSAGE);
            }
            List<ExpanseResponse> expanses = expanseService.getExpansesForTrip(tripId, connectedUser);
            Collections.sort(expanses);
            ExpanseInTripCurrency expanseInTripCurrency = ExpanseTotalCalculator.calculateInTripCurrency(expanses);
            List<ExpanseByCurrency> expanseByCurrency = getExpanseByCurrency(expanses);

            Document document = initializePdfDocument(tripPdfReport);
            document.add(tripReportParagraphCreator.getTitle(trip.getTripName()));
            document.add(expanseReportParagraphCreator.getBudget(
                    budget.getBudgetAmount(), expanseInTripCurrency, budget.getCurrency()));
            document.add(expanseReportParagraphCreator.getExpanseHeader(budget.getCurrency()));

            for (ExpanseResponse expanse : expanses) {
                document.add(expanseReportParagraphCreator.getExpanse(expanse, budget.getCurrency()));
            }

            document.add(expanseSummaryReportCreator.summaryByCurrency("Summary by currency"));
            document.add(expanseSummaryReportCreator.getSummaryHeader(budget.getCurrency()));
            for (ExpanseByCurrency expanse : expanseByCurrency) {
                document.add(expanseSummaryReportCreator.getSummary(expanse, budget.getCurrency()));
            }
            document.close();
            return tripPdfReport;
        } catch (Exception e) {
            handleException(e);
            return new ByteArrayOutputStream();
        }
    }

    private @NotNull Trip authenticateAndFetchTrip(Long tripId, UserDetails connectedUser) {
        Trip trip = tripService.getTripById(tripId);
        userAuthenticationService.validateOwnership(trip.getOwner(), connectedUser, USER_UNAUTHORIZED);
        return trip;
    }

    private @NotNull Document initializePdfDocument(ByteArrayOutputStream outputStream) {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        return new Document(pdfDocument);
    }

    private void processDay(@NotNull Document document, Day day) throws IOException {
        document.add(tripReportParagraphCreator.getDay(day));
        if (day.getNote() != null && !day.getNote().getContent().isBlank()) {
            document.add(tripReportParagraphCreator.getDayNote(day.getNote()));
        }
        document.add(tripReportParagraphCreator.getSeparatorLine());
        List<Activity> activities = new ArrayList<>(day.getActivities());
        Collections.sort(activities);
        for (Activity activity : activities) {
            processActivity(document, activity);
        }
    }

    private void processActivity(@NotNull Document document, @NotNull Activity activity) throws IOException {
        if (activity.getAddress() != null) {
            document.add(tripReportParagraphCreator.getActivityAddress(activity));
        }
        if (activity.getNote() != null) {
            document.add(tripReportParagraphCreator.getNote(activity.getNote()));
        }
        if (activity.getExpanse() != null) {
            document.add(tripReportParagraphCreator.getExpanse(activity.getExpanse()));
        }
        document.add(tripReportParagraphCreator.getSeparatorLine());
    }

    private void handleException(Exception e) {
        if (e instanceof BadCredentialsException) {
            throw new BadCredentialsException(USER_UNAUTHORIZED, e);
        } else if (e instanceof TripNotFoundException) {
            throw new TripNotFoundException(TRIP_NOT_FOUND_MESSAGE);
        } else if (e instanceof BudgetNotFoundException) {
            throw new BudgetNotFoundException(BUDGET_NOT_FOUND_MESSAGE);
        }
    }

    private @NotNull List<ExpanseByCurrency> getExpanseByCurrency(List<ExpanseResponse> expanses) {
        return BudgetMapper.toExpansesByCurrency(budgetService.calculateSumsByCurrency(expanses));
    }
}
