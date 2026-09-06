package dev.rajendra.orderflow.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Order(UUID id, String customerEmail, List<OrderItem> items, OrderStatus status,
                    BigDecimal total, Instant createdAt) {
    public Order { items = List.copyOf(items); }
}
