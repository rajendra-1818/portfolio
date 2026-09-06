package dev.rajendra.orderflow.order;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private final ConcurrentHashMap<UUID, Order> orders = new ConcurrentHashMap<>();

    public Order create(CreateOrderRequest request) {
        var items = request.items().stream().map(i -> new OrderItem(i.sku(), i.quantity(), i.unitPrice())).toList();
        var total = items.stream().map(OrderItem::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        var order = new Order(UUID.randomUUID(), request.customerEmail(), items, OrderStatus.CREATED, total, Instant.now());
        orders.put(order.id(), order);
        return order;
    }

    public Order find(UUID id) {
        var order = orders.get(id);
        if (order == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found");
        return order;
    }

    public List<Order> findAll(OrderStatus status) {
        return orders.values().stream().filter(o -> status == null || o.status() == status)
                .sorted(Comparator.comparing(Order::createdAt).reversed()).toList();
    }

    public Order transition(UUID id, OrderStatus next) {
        return orders.compute(id, (key, current) -> {
            if (current == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found");
            if (!allowed(current.status(), next)) throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "cannot transition from " + current.status() + " to " + next);
            return new Order(current.id(), current.customerEmail(), current.items(), next, current.total(), current.createdAt());
        });
    }

    private boolean allowed(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case CREATED -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.FULFILLED || to == OrderStatus.CANCELLED;
            case FULFILLED, CANCELLED -> false;
        };
    }
}
