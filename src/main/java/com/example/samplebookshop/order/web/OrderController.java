package com.example.samplebookshop.order.web;

import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.application.RichOrder;
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
import java.util.Map;

import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.*;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {

    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;

    //security: access for admins only
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> findAll() {
        return queryOrder.findAll();
    }

    //security: access for admins and the user who made the order
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> findOneById(@PathVariable Long id) {
        return queryOrder.findOneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //security: access for all users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> placeOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = this.manageOrder.placeOrder(command);
        URI uri = createOrderUri(placeOrderResponse.getOrderId());
        return ResponseEntity.created(uri).build();
    }

    //security: access for admins (all updates) and the user who made the order (only revoking the order)
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + status));
        //TODO: fix the email reference when implementing security features
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, "admin@example.org");
        this.manageOrder.updateOrderStatus(command);
    }

    //security: access for admins only
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manageOrder.deleteOrder(id);
    }

    private URI createOrderUri(Long orderId) {
        return new CustomUri("/" + orderId).toUri();
    }

}
