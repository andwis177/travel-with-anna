package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseTotalByBadge;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Budget Controller Tests")
class BudgetControllerTest {
    @MockBean
    private BudgetFacade budgetFacade;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);

        userDetails = createUserDetails(user);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getBudgetById_ShouldReturnBudget() throws Exception {
        // Given
        Long budgetId = 1L;
        BudgetResponse budgetResponse = new BudgetResponse(
                budgetId, "USD", BigDecimal.valueOf(500), 1L);
        when(budgetFacade.getBudgetById(eq(budgetId), any())).thenReturn(budgetResponse);

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
                ExpanseResponse.builder()
                        .expanseId(1L)
                        .expanseName("Food")
                        .expanseCategory("Eat")
                        .date("2024-12-01")
                        .currency("USD")
                        .price(BigDecimal.valueOf(100))
                        .paid(BigDecimal.valueOf(50))
                        .exchangeRate(BigDecimal.valueOf(1))
                        .priceInTripCurrency(BigDecimal.valueOf(100))
                        .paidInTripCurrency(BigDecimal.valueOf(50))
                        .build(),

                ExpanseResponse
                        .builder()
                        .expanseId(2L)
                        .expanseName("Transport")
                        .expanseCategory("Travel")
                        .date("2024-12-02")
                        .currency("USD")
                        .price(BigDecimal.valueOf(200))
                        .paid(BigDecimal.valueOf(100))
                        .exchangeRate(BigDecimal.valueOf(1))
                        .priceInTripCurrency(BigDecimal.valueOf(200))
                        .paidInTripCurrency(BigDecimal.valueOf(100))
                        .build()
        );

        BudgetExpensesRespond budgetExpensesRespond = getBudgetExpensesRespond(budgetResponse, expanses);
        when(budgetFacade.getBudgetExpanses(eq(tripId), eq(budgetId), any())).thenReturn(budgetExpensesRespond);

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
        doNothing().when(budgetFacade).updateBudget(budgetRequest, userDetails);

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
        when(budgetFacade.getExpansesByBadgeByTripId(eq(tripId), any())).thenReturn(expanseTotals);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/budget/calculate/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expanseTotals)));
    }

    private static @NotNull BudgetExpensesRespond getBudgetExpensesRespond(
            BudgetResponse budgetResponse, List<ExpanseResponse> expanses) {
        List<ExpanseByCurrency> sumsByCurrency = List.of(
                ExpanseByCurrency.builder().
                        currencyCode("USD").
                        totalPrice(BigDecimal.valueOf(300)).
                        totalPaid(BigDecimal.valueOf(150)).
                        totalPriceInTripCurrency(BigDecimal.valueOf(300)).
                        totalPaidInTripCurrency(BigDecimal.valueOf(150)).
                        totalDebt(BigDecimal.valueOf(150)).
                        build()
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

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}