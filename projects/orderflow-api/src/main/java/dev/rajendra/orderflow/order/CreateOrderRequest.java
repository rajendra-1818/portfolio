package dev.rajendra.orderflow.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(@NotBlank @Email String customerEmail,
                                 @NotEmpty @Size(max = 100) List<@NotNull @Valid Item> items) {
    public record Item(@NotBlank String sku, @Positive int quantity,
                       @NotNull @DecimalMin(value = "0.00") @Digits(integer = 10, fraction = 2) BigDecimal unitPrice) {}
}
