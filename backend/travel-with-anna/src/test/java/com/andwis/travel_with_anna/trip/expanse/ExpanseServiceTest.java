package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.CurrencyExchange;
import com.andwis.travel_with_anna.api.currency.CurrencyExchangeClient;
import com.andwis.travel_with_anna.api.currency.CurrencyRepository;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotFoundException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotSaveException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Expanse Service tests")
class ExpanseServiceTest {
    @InjectMocks
    private ExpanseService expanseService;
    @Mock
    private ExpanseRepository expanseRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CurrencyExchangeClient currencyExchangeService;
    @Mock
    private TripService tripService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ExpanseAuthorizationService expanseAuthorizationService;
    private Expanse expanse;
    private ExpanseRequest expanseRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);

        expanse = Expanse.builder()
                .expanseName("Hotel Booking")
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(50.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();

        expanseRequest = ExpanseRequest.builder()
                .expanseId(1L)
                .entityId(2L)
                .tripId(3L)
                .currency("USD")
                .price(BigDecimal.valueOf(100.00))
                .paid(BigDecimal.valueOf(50.00))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();

        userDetails = createUserDetails(user);
    }

    @Test
    void testSaveExpanse() {
        // Given
        when(expanseRepository.save(expanse)).thenReturn(expanse);

        // When
        Expanse savedExpanse = expanseService.save(expanse);

        // Then
        assertNotNull(savedExpanse);
        assertEquals("Hotel Booking", savedExpanse.getExpanseName());
        verify(expanseRepository, times(1)).save(expanse);
    }

    @Test
    void testFindByIdFound() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        Expanse foundExpanse = expanseService.findById(1L);

        // Then
        assertNotNull(foundExpanse);
        assertEquals(expanse, foundExpanse);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(ExpanseNotFoundException.class, () ->
                expanseService.findById(1L));

        // Then
        assertEquals("Expanse not found", exception.getMessage());
    }

    @Test
    void testGetCurrencyExchangeByCode() {
        // Given
        CurrencyExchange currencyExchange =  CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timestamp(LocalDateTime.of(
                        2024, 1, 1, 0, 0))
                .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currencyExchange));

        // When
        CurrencyExchange foundExchange = expanseService.getCurrencyExchangeByCode("USD");

        // Then
        assertNotNull(foundExchange);
        assertEquals("USD", foundExchange.getCode());
    }

    @Test
    void testGetCurrencyExchangeByCodeNotFound() {
        // Given
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CurrencyNotProvidedException.class,
                () -> expanseService.getCurrencyExchangeByCode("EUR"));

        // Then
        assertEquals("EUR", exception.getMessage());
    }

    @Test
    @Transactional
    void testCreateOrUpdateExpanse_Create() {
        // Given
        ExpanseRequest expanseRequest = ExpanseRequest.builder()
                .expanseId(null)
                .entityId(2L)
                .tripId(3L)
                .expanseName("Hotel Booking")
                .currency("USD")
                .price(BigDecimal.valueOf(200.00))
                .paid(BigDecimal.valueOf(150.00))
                .exchangeRate(BigDecimal.valueOf(1.5))
                .build();

        when(expanseRepository.save(any())).thenReturn(expanse);
        when(tripService.getTripById(3L)).thenReturn(new Trip());

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(expanseRequest,
                id -> new Object(),
                () -> expanse,
                (object, expanse) -> {},
                userDetails);

        // Then
        assertNotNull(response);
        verify(expanseRepository).save(expanse);
    }

    @Test
    void testCreateOrUpdateExpanse_Update() {
        // Given
        when(expanseRepository.save(any(Expanse.class))).thenReturn(expanse);
        when(expanseRepository.findById(anyLong())).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.createOrUpdateExpanse(expanseRequest,
                id -> new Object(),
                Expanse::new,
                (object, expanse) -> {},
                userDetails);

        // Then
        assertNotNull(response);
        verify(expanseRepository).save(any(Expanse.class));
    }

    @Test
    void testCreateOrUpdateExpanseNotSaved() {
        // Given
        expanseRequest.setExpanseId(null);
        expanseRequest.setEntityId(null);
        expanseRequest.setTripId(null);

        // When
        Exception exception = assertThrows(ExpanseNotSaveException.class, () ->
                expanseService.createOrUpdateExpanse(expanseRequest,
                        id -> new Object(),
                        Expanse::new,
                        (object, expanse) -> {},
                        userDetails));

        // Then
        assertEquals("Expanse not saved", exception.getMessage());
    }

    @Test
    void testGetExpanseById_Found() {
        // Given
        when(expanseRepository.findById(1L)).thenReturn(Optional.of(expanse));

        // When
        ExpanseResponse response = expanseService.getExpanseById(1L, userDetails);

        // Then
        assertNotNull(response);
        assertEquals(expanse.getExpanseId(), response.getExpanseId());
        verify(expanseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetExpanseById_NotFound() {
        // Given
        // When
        Exception response = assertThrows(ExpanseNotFoundException.class, () ->
                expanseService.getExpanseById(101L, userDetails));

        // Then
        assertEquals("Expanse not found", response.getMessage());
        verify(expanseRepository, times(1)).findById(101L);
    }

    @Test
    void testGetExpansesForTrip() {
        // Given
        when(expanseRepository.findByTripId(3L)).thenReturn(List.of(expanse));

        // When
        List<ExpanseResponse> responseList = expanseService.getExpansesForTrip(3L, userDetails);

        // Then
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals(expanse.getExpanseId(), responseList.getFirst().getExpanseId());
        verify(expanseRepository, times(1)).findByTripId(3L);
    }

    @Test
    void testGetExpanseByEntityId_EntityWithExpanse() {
        // Given
        Long entityId = 2L;
        Function<Long, Object> getByIdFunction = id -> new Object();
        Function<Object, Expanse> getExpanseFunction = object -> expanse;

        // When
        ExpanseResponse response = expanseService.getExpanseByEntityId(entityId, getByIdFunction, getExpanseFunction, userDetails);

        // Then
        assertNotNull(response);
        assertEquals(expanse.getExpanseId(), response.getExpanseId());
    }

    @Test
    void testGetExpanseByEntityId_EntityWithoutExpanse() {
        // Given
        Long entityId = 2L;
        Function<Long, Object> getByIdFunction = id -> new Object();
        Function<Object, Expanse> getExpanseFunction = object -> null;

        // When
        ExpanseResponse response = expanseService.getExpanseByEntityId(
                entityId, getByIdFunction, getExpanseFunction, userDetails);

        // Then
        assertNull(response);
    }

    @Test
    void testGetExchangeRate_WhenCurrencyFromIsNullOrBlank() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate(null, "USD");
        ExchangeResponse response2 = expanseService.getExchangeRate("", "USD");

        // Then
        assertNotNull(response);
        assertEquals("Currency not provided", response.errorMsg());
        assertEquals(BigDecimal.ZERO, response.exchangeRate());

        assertNotNull(response2);
        assertEquals("Currency not provided", response2.errorMsg());
        assertEquals(BigDecimal.ZERO, response2.exchangeRate());
    }


    @Test
    void testGetExchangeRate_WhenCurrencyToIsNullOrBlank() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", null);
        ExchangeResponse response2 = expanseService.getExchangeRate("USD", "");

        // Then
        assertNotNull(response);
        assertEquals("Currency not provided", response.errorMsg());
        assertEquals(BigDecimal.ZERO, response.exchangeRate());

        assertNotNull(response2);
        assertEquals("Currency not provided", response2.errorMsg());
        assertEquals(BigDecimal.ZERO, response2.exchangeRate());
    }

    @Test
    void testGetExchangeRate_WhenCurrencyFromEqualsCurrencyTo() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "USD");

        // Then
        assertNotNull(response);
        assertNull(response.errorMsg());
        assertEquals(BigDecimal.ONE, response.exchangeRate());
    }

    @Test
    void testGetExchangeRate_WhenCurrencyFromAndToAreValid() {
        // Given
        CurrencyExchange usdExchange = CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timestamp(LocalDateTime.now())
                .build();
        CurrencyExchange eurExchange = CurrencyExchange.builder()
                .code("EUR")
                .exchangeValue(BigDecimal.valueOf(0.85))
                .timestamp(LocalDateTime.now())
                .build();
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdExchange));
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(eurExchange));

        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(response);
        assertNull(response.errorMsg());
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(response.exchangeRate()));
    }

    @Test
    void testGetExchangeRate_WhenCurrencyNotFound() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "XYZ");

        // Then
        assertNotNull(response);
        assertEquals("Currency not supported", response.errorMsg());
        assertEquals(BigDecimal.ZERO, response.exchangeRate());
    }

    @Test
    void testGetExchangeRate_WhenCurrenciesAreFetchedFromService() {
        // Given
        CurrencyExchange usdExchange = CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timestamp(LocalDateTime.now())
                .build();
        CurrencyExchange eurExchange = CurrencyExchange.builder()
                .code("EUR")
                .exchangeValue(BigDecimal.valueOf(0.85))
                .timestamp(LocalDateTime.now())
                .build();

        when(currencyRepository.count()).thenReturn(1L);

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdExchange));
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(eurExchange));

        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(response);
        assertNull(response.errorMsg());
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(response.exchangeRate()));
    }

    @Test
    void testGetExchangeRate_WhenCurrencyUpdateIsRequired() {
        // Given
        when(currencyRepository.count()).thenReturn(1L);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(CurrencyExchange.builder()
                .code("USD")
                .exchangeValue(BigDecimal.valueOf(1.0))
                .timestamp(LocalDateTime.now().minusHours(1))
                .build()));

        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(CurrencyExchange.builder()
                .code("EUR")
                .exchangeValue(BigDecimal.valueOf(0.85))
                .timestamp(LocalDateTime.now().minusHours(1))
                .build()));

        // When
        ExchangeResponse response = expanseService.getExchangeRate("USD", "EUR");

        // Then
        assertNotNull(response);
        assertNull(response.errorMsg());
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(response.exchangeRate()));
    }


    @Test
    void testGetExchangeRateWithNullCurrency() {
        // Given
        // When
        ExchangeResponse response = expanseService.getExchangeRate(null, null);

        // Then
        assertEquals("Currency not provided", response.errorMsg());
        assertEquals(BigDecimal.ZERO, response.exchangeRate());
    }

    @Test
    void testGetExpanseInTripCurrency() {
        // Given
        TripCurrencyValuesRequest request = new TripCurrencyValuesRequest(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(1.2)
        );
        // When

        ExpanseInTripCurrency inTripCurrency = expanseService.getExpanseInTripCurrency(request);

        // Then
        assertNotNull(inTripCurrency);
        assertEquals(0,inTripCurrency.price().compareTo(BigDecimal.valueOf(120)));
        assertEquals(0, inTripCurrency.paid().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    void testChangeTripCurrency() {
        // Given
        Budget budget = new Budget();
        Trip trip = new Trip();
        trip.setTripId(1L);
        budget.setTrip(trip);

        when(expanseRepository.findByTripId(1L)).thenReturn(List.of(expanse));
        when(expanseRepository.saveAll(anyList())).thenReturn(List.of(expanse));

        // When
        expanseService.changeTripCurrency(budget);

        // Then
        verify(expanseRepository).saveAll(anyList());
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