package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Budget Controller Tests")
class BudgetControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BudgetFacade budgetFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testSaveBudget_ShouldReturnAccepted() throws Exception {
        // Given
        Budget budget = new Budget();
        budget.setCurrency("USD");
        budget.setToSpend(BigDecimal.valueOf(1000));
        String requestBody = objectMapper.writeValueAsString(budget);
        doNothing().when(budgetFacade).saveBudget(budget);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/budget")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getBudgetById_ShouldReturnBudget() throws Exception {
        // Given
        Long budgetId = 1L;
        BudgetResponse budgetResponse = new BudgetResponse(
                budgetId, "USD", BigDecimal.valueOf(500), 1L);
        when(budgetFacade.getBudgetById(budgetId)).thenReturn(budgetResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/budget/{budgetId}", budgetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(budgetResponse)));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getBudgetExpanses_ShouldReturnBudgetExpanses() throws Exception {
        // Given
        Long tripId = 2L;
        Long budgetId = 1L;
        BudgetResponse budgetResponse = new BudgetResponse(
                budgetId, "USD", BigDecimal.valueOf(1000), 1L);
        List<ExpanseResponse> expanses = List.of(
                new ExpanseResponse(
                        1L, "Food", "USD", BigDecimal.valueOf(100), BigDecimal.valueOf(50),
                        BigDecimal.valueOf(1), BigDecimal.valueOf(100), BigDecimal.valueOf(50)
                ),
                new ExpanseResponse(
                        2L, "Transport", "USD", BigDecimal.valueOf(200), BigDecimal.valueOf(100),
                        BigDecimal.valueOf(1), BigDecimal.valueOf(200), BigDecimal.valueOf(100)
                )
        );

        BudgetExpensesRespond budgetExpensesRespond = getBudgetExpensesRespond(budgetResponse, expanses);
        when(budgetFacade.getBudgetExpanses(tripId, budgetId)).thenReturn(budgetExpensesRespond);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/budget/{budgetId}/expanses/{tripId}", budgetId, tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(budgetExpensesRespond)));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void updateBudget_ShouldReturnOk() throws Exception {
        // Given
        BudgetRequest budgetRequest = new BudgetRequest(
                1L, "USD", BigDecimal.valueOf(1000), 1L);
        String requestBody = objectMapper.writeValueAsString(budgetRequest);
        doNothing().when(budgetFacade).updateBudget(budgetRequest);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/budget/update")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getExpansesByBadgeByTripId_ShouldReturnExpanses() throws Exception {
        // Given
        Long tripId = 1L;
        List<ExpanseTotalByBadge> expanseTotals = List.of(
                new ExpanseTotalByBadge("Food", BigDecimal.valueOf(150), BigDecimal.valueOf(75)),
                new ExpanseTotalByBadge("Transport", BigDecimal.valueOf(200), BigDecimal.valueOf(100))
        );
        when(budgetFacade.getExpansesByBadgeByTripId(tripId)).thenReturn(expanseTotals);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/budget/calculate/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expanseTotals)));
    }

    private static @NotNull BudgetExpensesRespond getBudgetExpensesRespond(BudgetResponse budgetResponse, List<ExpanseResponse> expanses) {
        List<ExpanseByCurrency> sumsByCurrency = List.of(
                new ExpanseByCurrency(
                        "USD", BigDecimal.valueOf(300), BigDecimal.valueOf(150),
                        BigDecimal.valueOf(300), BigDecimal.valueOf(150), BigDecimal.valueOf(150)
                )
        );

        return new BudgetExpensesRespond(
                budgetResponse,
                expanses,
                sumsByCurrency,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50)
        );
    }
}