package dev.rajendra.orderflow.order;

import java.math.BigDecimal;

public record OrderItem(String sku, int quantity, BigDecimal unitPrice) {
    public OrderItem {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be positive");
        if (unitPrice.signum() < 0) throw new IllegalArgumentException("unitPrice cannot be negative");
    }
    public BigDecimal total() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
