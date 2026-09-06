package dev.rajendra.orderflow.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    private final OrderService service = new OrderService();

    private Order create() {
        return service.create(new CreateOrderRequest("engineer@example.com", List.of(
            new CreateOrderRequest.Item("keyboard", 2, new BigDecimal("49.95")))));
    }

    @Test void completesValidWorkflowAndRejectsChangesAfterFulfillment() {
        var order = create();
        assertEquals(new BigDecimal("99.90"), order.total());
        assertEquals(OrderStatus.CONFIRMED, service.transition(order.id(), OrderStatus.CONFIRMED).status());
        assertEquals(OrderStatus.FULFILLED, service.transition(order.id(), OrderStatus.FULFILLED).status());
        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> service.transition(order.id(), OrderStatus.CANCELLED)).getStatusCode().value());
    }

    @Test void rejectsSkippedTransitionWithoutChangingOrder() {
        var order = create();
        assertThrows(ResponseStatusException.class, () -> service.transition(order.id(), OrderStatus.FULFILLED));
        assertEquals(OrderStatus.CREATED, service.find(order.id()).status());
    }

    @Test void cancelledOrdersAreTerminalAndCanBeFiltered() {
        var order = create();
        service.transition(order.id(), OrderStatus.CANCELLED);
        assertEquals(1, service.findAll(OrderStatus.CANCELLED).size());
        assertTrue(service.findAll(OrderStatus.CREATED).isEmpty());
        assertThrows(ResponseStatusException.class, () -> service.transition(order.id(), OrderStatus.CONFIRMED));
    }

    @Test void returnsNotFoundForUnknownOrders() {
        assertEquals(404, assertThrows(ResponseStatusException.class,
            () -> service.find(UUID.randomUUID())).getStatusCode().value());
    }
}
