package com.example.samplebookshop.order.web;

import com.example.samplebookshop.order.application.RichOrder;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.security.UserSecurity;
import com.example.samplebookshop.web.CustomUri;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.UpdateStatusCommand;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {

    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> findAll() {
        return queryOrder.findAll();
    }

    //security: access for admins and the user who made the order
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> findOneById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return queryOrder.findOneById(id)
                .map(order -> authorize(order, user))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorize(RichOrder order, @AuthenticationPrincipal User user){
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)){
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> placeOrder(@RequestBody PlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = this.manageOrder.placeOrder(command);
        URI uri = createOrderUri(placeOrderResponse.getOrderId());
        return ResponseEntity.created(uri).build();
    }

    //security: access for admins (all updates) and the user who made the order (only revoking the order)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/status")
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

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manageOrder.deleteOrder(id);
    }

    private URI createOrderUri(Long orderId) {
        return new CustomUri("/" + orderId).toUri();
    }

}
