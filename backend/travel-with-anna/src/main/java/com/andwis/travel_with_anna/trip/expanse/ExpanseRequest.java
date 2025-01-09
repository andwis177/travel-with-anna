package com.andwis.travel_with_anna.trip.expanse;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.andwis.travel_with_anna.trip.expanse.Expanse.*;

@Getter
@Setter
@Builder
public class ExpanseRequest {

       private Long expanseId;

       private Long entityId;

       private Long tripId;

       @Size(max = MAX_EXPANSE_NAME_LENGTH, message = "Expanse name should be " + MAX_EXPANSE_NAME_LENGTH + " characters or less")
       private String expanseName;

       @NotNull(message = "Expanse Category must not be null")
       @Size(max = MAX_CATEGORY_LENGTH, message = "Expanse category should be " + MAX_CATEGORY_LENGTH + " characters or less")
       private String expanseCategory;

       private String date;

       @NotNull(message = "Currency field is required")
       @NotEmpty(message = "Currency is required")
       @NotBlank(message = "Currency is required")
       @Size(max = MAX_CURRENCY_LENGTH, message = "Currency description should be " + MAX_CURRENCY_LENGTH + " characters or less")
       private String currency;

       @NotNull(message = "Price field is required")
       @DecimalMin(value = "0.0", message = "Price amount must be greater than or equal to 0")
       private BigDecimal price;

       @NotNull(message = "Paid field is required")
       @DecimalMin(value = "0.0", message = "Paid amount must be greater than or equal to 0")
       private BigDecimal paid;

       @NotNull(message = "Exchange rate is required")
       @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be greater than 0")
       private BigDecimal exchangeRate;

       private BigDecimal priceInTripCurrency;

       private BigDecimal paidInTripCurrency;

       private String entityType;
}
