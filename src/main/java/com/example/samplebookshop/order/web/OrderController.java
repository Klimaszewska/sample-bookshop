package com.example.samplebookshop.order.web;

import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase.RichOrder;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.web.CustomUri;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {

    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> findAll() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> findOneById(@PathVariable Long id) {
        return queryOrder.findOneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> placeOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = this.manageOrder.placeOrder(command);
        URI uri = createOrderUri(placeOrderResponse.getOrderId());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody RestUpdateOrderStatusCommand command) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.orderStatus)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + command.orderStatus));
        this.manageOrder.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manageOrder.deleteOrder(id);
    }

    private URI createOrderUri(Long orderId) {
        return new CustomUri("/" + orderId).toUri();
    }

    @Data
    private static class RestUpdateOrderStatusCommand {
        String orderStatus;
    }
}
