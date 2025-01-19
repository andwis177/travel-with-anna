package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Expanse Controller Tests")
class ExpanseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ExpanseFacade expanseFacade() {
            return Mockito.mock(ExpanseFacade.class);
        }
    }

    @Autowired
    private ExpanseFacade expanseFacade;

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
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testCreateOrUpdateExpanse_ShouldReturnCreatedAndExpanseResponse() throws Exception {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .expanseName("Hotel Booking")
                .expanseCategory("Hotel")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(80.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120.00))
                .paidInTripCurrency(BigDecimal.valueOf(96.00))
                .build();

        ExpanseResponse response = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Hotel Booking")
                .expanseCategory("Hotel")
                .date("2022-01-01")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(80.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120.00))
                .paidInTripCurrency(BigDecimal.valueOf(96.00))
                .build();

        when(expanseFacade.createOrUpdateExpanse(any(ExpanseRequest.class), any())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/expanse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExpanseById_ShouldReturnExpanseResponse() throws Exception {
        // Given
        Long expanseId = 1L;
        ExpanseResponse response = ExpanseResponse.builder()
                .expanseId(expanseId)
                .expanseName("Hotel Booking")
                .expanseCategory("Hotel")
                .date("2022-01-01")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(80.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120.00))
                .paidInTripCurrency(BigDecimal.valueOf(96.00))
                .build();

        when(expanseFacade.getExpanseById(eq(expanseId), any())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/{expanseId}", expanseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExpanseByEntity_ShouldReturnExpanseResponse() throws Exception {
        // Given
        Long entityId = 1L;
        String entityType = "ITEM";
        ExpanseResponse response = ExpanseResponse.builder()
                .expanseId(entityId)
                .expanseName("Hotel Booking")
                .expanseCategory("Hotel")
                .date("2022-01-01")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(80.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120.00))
                .paidInTripCurrency(BigDecimal.valueOf(96.00))
                .build();

        when(expanseFacade.getExpanseByEntityId(eq(entityId), eq(entityType), any())).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/{entityId}/type/{entityType}", entityId, entityType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetExchangeRate_ShouldReturnExchangeRate() throws Exception {
        // Given
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        ExchangeResponse exchangeResponse = new ExchangeResponse("", BigDecimal.valueOf(1.18));

        when(expanseFacade.getExchangeRate(currencyFrom, currencyTo)).thenReturn(exchangeResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expanse/exchange")
                        .param("currencyFrom", currencyFrom)
                        .param("currencyTo", currencyTo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(exchangeResponse)));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGetTripCurrencyValues_ShouldReturnExpanseInTripCurrency() throws Exception {
        // Given
        BigDecimal price = BigDecimal.valueOf(500.00);
        BigDecimal paid = BigDecimal.valueOf(400.00);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.20);

        ExpanseInTripCurrency expanseInTripCurrency = new ExpanseInTripCurrency(
                BigDecimal.valueOf(600.00),
                BigDecimal.valueOf(480.00)
        );

        TripCurrencyValuesRequest request = new TripCurrencyValuesRequest(price, paid, exchangeRate);

        when(expanseFacade.getExpanseInTripCurrency(eq(request))).thenReturn(expanseInTripCurrency);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/expanse/trip-currency-values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expanseInTripCurrency)));
    }
}