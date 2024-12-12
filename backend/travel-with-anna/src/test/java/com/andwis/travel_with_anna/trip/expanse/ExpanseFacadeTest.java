package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpanseFacadeTest {
    @InjectMocks
    private ExpanseFacade expanseFacade;
    @Mock
    private ExpanseService expanseService;
    @Mock
    private ItemService itemService;
    @Mock
    private ActivityService activityService;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);

        userDetails = createUserDetails(user);
    }

    @Test
    void testCreateOrUpdateExpanse_Item() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .entityId(1L)
                .entityType("item")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(50))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();
        Item mockItem = new Item();

        ExpanseResponse response = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Test Expanse")
                .expanseCategory("Category")
                .date("2021-08-01")
                .currency("USD")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(50))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120))
                .paidInTripCurrency(BigDecimal.valueOf(60))
                .build();

        when(itemService.findById(request.getEntityId())).thenReturn(mockItem);
        when(expanseService.createOrUpdateExpanse(any(), any(), any(), any(), any()))
                .thenReturn(response);

        // When
        ExpanseResponse updateResponse = expanseFacade.createOrUpdateExpanse(request, userDetails);

        // Then
        assertNotNull(updateResponse);
        assertEquals("Test Expanse", updateResponse.getExpanseName());
        verify(expanseService, times(1)).createOrUpdateExpanse(any(), any(), any(), any(), any());
    }

    @Test
    void testCreateOrUpdateExpanse_Activity() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .entityId(1L)
                .entityType("activity")
                .price(BigDecimal.valueOf(150))
                .paid(BigDecimal.valueOf(70))
                .exchangeRate(BigDecimal.valueOf(1.3))
                .build();

        ExpanseResponse response = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Test Activity Expanse")
                .expanseCategory("Category")
                .date("2021-08-01")
                .currency("USD")
                .price(BigDecimal.valueOf(150))
                .paid(BigDecimal.valueOf(70))
                .exchangeRate(BigDecimal.valueOf(1.3))
                .priceInTripCurrency(BigDecimal.valueOf(195))
                .paidInTripCurrency(BigDecimal.valueOf(91))
                .build();

        Activity mockActivity = new Activity();
        when(activityService.getById(request.getEntityId())).thenReturn(mockActivity);
        when(expanseService.createOrUpdateExpanse(any(), any(), any(), any(), any()))
                .thenReturn(response);

        // When
        ExpanseResponse updateResponse = expanseFacade.createOrUpdateExpanse(request, any());

        // Then
        assertNotNull(updateResponse);
        assertEquals("Test Activity Expanse", updateResponse.getExpanseName());
        verify(expanseService, times(1)).createOrUpdateExpanse(any(), any(), any(), any(), any());
    }

    @Test
    void testGetExpanseByEntityId_Item() {
        // Given
        Long entityId = 1L;
        String entityType = "item";
        Item mockItem = new Item();

        ExpanseResponse expanseResponse = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Item Expanse")
                .expanseCategory("Category")
                .date("2021-08-01")
                .currency("USD")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(50))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .priceInTripCurrency(BigDecimal.valueOf(120))
                .paidInTripCurrency(BigDecimal.valueOf(60))
                .build();

        when(itemService.findById(entityId)).thenReturn(mockItem);
        when(expanseService.getExpanseByEntityId(anyLong(), any(), any(), any()))
                .thenReturn(expanseResponse);

        // When
        ExpanseResponse response = expanseFacade.getExpanseByEntityId(entityId, entityType, userDetails);

        // Then
        assertNotNull(response);
        assertEquals("Item Expanse", response.getExpanseName());
        verify(expanseService, times(1)).getExpanseByEntityId(anyLong(), any(), any(), any());
    }

    @Test
    void testGetExpanseByEntityId_Activity() {
        // Given
        Long entityId = 2L;
        String entityType = "activity";
        Activity mockActivity = new Activity();

        ExpanseResponse expanseResponse = ExpanseResponse.builder()
                .expanseId(2L)
                .expanseName("Activity Expanse")
                .expanseCategory("Category")
                .date("2021-08-01")
                .currency("USD")
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(80))
                .exchangeRate(BigDecimal.valueOf(1.5))
                .priceInTripCurrency(BigDecimal.valueOf(300))
                .paidInTripCurrency(BigDecimal.valueOf(120))
                .build();

        when(activityService.getById(entityId)).thenReturn(mockActivity);
        when(expanseService.getExpanseByEntityId(anyLong(), any(), any(), any()))
                .thenReturn(expanseResponse);

        // When
        ExpanseResponse response = expanseFacade.getExpanseByEntityId(entityId, entityType, userDetails);

        // Then
        assertNotNull(response);
        assertEquals("Activity Expanse", response.getExpanseName());
        verify(expanseService, times(1)).getExpanseByEntityId(anyLong(), any(), any(), any());
    }

    @Test
    void testGetExchangeRate() {
        // Given
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        ExchangeResponse mockResponse = new ExchangeResponse("", BigDecimal.valueOf(0.85));
        when(expanseService.getExchangeRate(currencyFrom, currencyTo)).thenReturn(mockResponse);

        // When
        ExchangeResponse response = expanseFacade.getExchangeRate(currencyFrom, currencyTo);

        // Then
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(0.85), response.exchangeRate());
        verify(expanseService, times(1)).getExchangeRate(currencyFrom, currencyTo);
    }

    @Test
    void testGetExpanseInTripCurrency() {
        // Given
        BigDecimal price = BigDecimal.valueOf(100);
        BigDecimal paid = BigDecimal.valueOf(50);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);

        ExpanseInTripCurrency expanseInTripCurrency = new ExpanseInTripCurrency(BigDecimal.valueOf(120), BigDecimal.valueOf(60));
        TripCurrencyValuesRequest request = new TripCurrencyValuesRequest(price, paid, exchangeRate);

        when(expanseService.getExpanseInTripCurrency(eq(request))).thenReturn(expanseInTripCurrency);

        // When
        ExpanseInTripCurrency result = expanseFacade.getExpanseInTripCurrency(request);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(120), result.price());
        assertEquals(BigDecimal.valueOf(60), result.paid());
        verify(expanseService, times(1)).getExpanseInTripCurrency(request);
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