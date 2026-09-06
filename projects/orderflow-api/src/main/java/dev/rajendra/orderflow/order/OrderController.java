package dev.rajendra.orderflow.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Order create(@Valid @RequestBody CreateOrderRequest request) { return service.create(request); }
    @GetMapping
    public List<Order> findAll(@RequestParam(required = false) OrderStatus status) { return service.findAll(status); }
    @GetMapping("/{id}") public Order find(@PathVariable UUID id) { return service.find(id); }
    @PostMapping("/{id}/transitions/{status}")
    public Order transition(@PathVariable UUID id, @PathVariable OrderStatus status) { return service.transition(id, status); }
}
