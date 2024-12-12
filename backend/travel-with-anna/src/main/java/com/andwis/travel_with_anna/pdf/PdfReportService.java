package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.handler.exception.PdfReportCreationException;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {
    private static final String USER_UNAUTHORIZED = "You are not authorized to access this trip";
    private final TripReportCreator tripReportParagraphCreator;
    private final ExpanseReportCreator expanseReportParagraphCreator;
    private final TripService tripService;
    private final ExpanseService expanseService;
    private final UserAuthenticationService userAuthenticationService;
    private final ExpanseSummaryReportCreator expanseSummaryReportCreator;
    private final BudgetService budgetService;

    public ByteArrayOutputStream createTripPdfReport(Long tripId, UserDetails connectedUser) throws PdfReportCreationException {
        ByteArrayOutputStream tripPdfReport = new ByteArrayOutputStream();
        try {
            Trip trip = tripService.getTripById(tripId);
            userAuthenticationService.verifyOwner(trip.getOwner(), connectedUser, USER_UNAUTHORIZED);
            List<Day> days = trip.getDaysInOrder();

            PdfWriter writer = new PdfWriter(tripPdfReport);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(tripReportParagraphCreator.getTitle(trip.getTripName()));
            document.add(tripReportParagraphCreator.getDates(
                    days.getFirst().getDate().toString(),
                    days.getLast().getDate().toString()));

            for (Day day : days) {
                document.add(tripReportParagraphCreator.getDay(day));
                if (
                        day.getNote() != null
                        && day.getNote().getNote() != null
                        && !day.getNote().getNote().isBlank()
                ) {
                    document.add(tripReportParagraphCreator.getDayNote(day.getNote()));
                }
                document.add(tripReportParagraphCreator.getSeparatorLine());
                List<Activity> activities = new ArrayList<>(day.getActivities());
                Collections.sort(activities);
                for (Activity activity : activities) {
                    document.add(tripReportParagraphCreator.getActivity(activity));
                    document.add(tripReportParagraphCreator.getActivityAddress(activity));
                    if (activity.getNote() != null) {
                        document.add(tripReportParagraphCreator.getNote(
                                activity.getNote()));
                    }
                    if (activity.getExpanse() != null) {
                        document.add(tripReportParagraphCreator.getExpanse(activity.getExpanse()));
                        document.add(tripReportParagraphCreator.getSeparatorLine());
                    } else {
                        document.add(tripReportParagraphCreator.getSeparatorLine());
                    }
                }
            }
            document.close();
            return tripPdfReport;
        }
        catch (BadCredentialsException e) {
            throw e;
        } catch (TripNotFoundException e) {
            throw new TripNotFoundException("Trip not found");

        } catch (Exception e) {
            throw new PdfReportCreationException("Error while creating Trip PDF report");
        }
    }

    public ByteArrayOutputStream createExpansePdfReport(Long tripId, UserDetails connectedUser) throws PdfReportCreationException {
        List<ExpanseByCurrency> expanseByCurrency = getExpanseByCurrency(expanseService.getExpansesForTrip(tripId, connectedUser));
        ByteArrayOutputStream tripPdfReport = new ByteArrayOutputStream();
        try {
            Trip trip = tripService.getTripById(tripId);
            userAuthenticationService.verifyOwner(trip.getOwner(), connectedUser, USER_UNAUTHORIZED);
            Budget budget = trip.getBudget();
            List<ExpanseResponse> expanses = expanseService.getExpansesForTrip(tripId, connectedUser);
            Collections.sort(expanses);
            ExpanseInTripCurrency expanseInTripCurrency = ExpanseTotalCalculator.calculateInTripCurrency(expanses);

            PdfWriter writer = new PdfWriter(tripPdfReport);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(tripReportParagraphCreator.getTitle(trip.getTripName()));
            document.add(expanseReportParagraphCreator.getBudget(
                    budget.getToSpend(),
                    expanseInTripCurrency,
                    budget.getCurrency()));
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
        }
        catch (BadCredentialsException e) {
            throw e;
        } catch (TripNotFoundException e) {
            throw new TripNotFoundException("Trip not found");

        } catch (Exception e) {
            throw new PdfReportCreationException("Error while creating Trip PDF report");
        }
    }

    private @NotNull List<ExpanseByCurrency> getExpanseByCurrency(List<ExpanseResponse> expanses) {
        return BudgetMapper.toExpansesByCurrency(budgetService.calculateSumsByCurrency(expanses));
    }
}
