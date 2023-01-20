package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.order.domain.Recipient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.OrderItemCommand;
import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTestIT {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private ManageOrderService manageOrderService;

    @Autowired
    private QueryOrderUseCase queryOrderUseCase;

    @Autowired
    private CatalogUseCase catalogUseCase;

    @Test
    void userCanPlaceOrder() {
        //given
        Book sampleBookOne = givenSampleBookOne(50L);
        Book sampleBookTwo = givenSampleBookTwo(50L);

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(createRecipient())
                .item(new OrderItemCommand(sampleBookOne.getId(), 15))
                .item(new OrderItemCommand(sampleBookTwo.getId(), 10))
                .build();

        //when
        PlaceOrderResponse response = manageOrderService.placeOrder(command);

        //then
        assertTrue(response.isSuccess());
        assertEquals(35L, catalogUseCase.findOneById(sampleBookOne.getId()).get().getAvailableBooks());
        assertEquals(40L, catalogUseCase.findOneById(sampleBookTwo.getId()).get().getAvailableBooks());
    }

    @Test
    void shouldThrowExceptionWhenOrderingMoreBooksThanAvailable() {
        Book sampleBookOne = givenSampleBookOne(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(createRecipient())
                .item(new OrderItemCommand(sampleBookOne.getId(), 10))
                .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            manageOrderService.placeOrder(command);
        });

        assertTrue(exception.getMessage().equalsIgnoreCase("Too many copies of book "
                + sampleBookOne.getId() + " requested: 10 of 5 available"));
    }

    @Test
    void userCanRevokeOrder(){
        // given
        Book sampleBookOne = givenSampleBookOne(50L);
        Long orderId = placeOrder(sampleBookOne.getId(), 15);
        assertEquals(35L, getAvailableBooks(sampleBookOne));

        // when
        manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        // then
        assertEquals(OrderStatus.CANCELLED, queryOrderUseCase.findOneById(orderId).get().getStatus());
        assertEquals(50L, getAvailableBooks(sampleBookOne));

    }

    @Test
    void userCannotRevokePaidOrder() {
        // given
        Long orderId = generateSamplePaidOrder();

        // when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        });

        // then
        assertTrue(exception.getMessage().equalsIgnoreCase("Unable to mark " + OrderStatus.PAID + " order as " + OrderStatus.CANCELLED));
    }

    @Test
    void userCannotRevokeShippedOrder() {
        // given
        Long orderId = generateSamplePaidOrder();
        manageOrderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

        // when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            manageOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        });

        // then
        assertTrue(exception.getMessage().equalsIgnoreCase("Unable to mark " + OrderStatus.SHIPPED + " order as " + OrderStatus.CANCELLED));
    }

    @Test
    void userCannotOrderNonExistingBooks() {
        //given
        Long invalidId = -1L;

        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            placeOrder(invalidId, 15);
        });

        //then
        Assertions.assertTrue(exception.getMessage().contains("Unable to find"));

    }

    @Test
    void userCannotOrderNegativeNumberOfBooks() {
        //given
        Book sampleBookOne = givenSampleBookOne(50L);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            placeOrder(sampleBookOne.getId(), -10);
        });

        //then
        assertTrue(exception.getMessage().equalsIgnoreCase("Quantity must be more than 0"));
        assertEquals(50, getAvailableBooks(sampleBookOne));
    }

    private Long placeOrder(Long bookId, int quantity){
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(createRecipient())
                .item(new OrderItemCommand(bookId, quantity))
                .build();

        PlaceOrderResponse response = manageOrderService.placeOrder(command);
        return response.getOrderId();
    }

    private Book givenSampleBookTwo(long available) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenSampleBookOne(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Long getAvailableBooks(Book sampleBookOne) {
        return catalogUseCase.findOneById(sampleBookOne.getId()).get().getAvailableBooks();
    }

    private Recipient createRecipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Long generateSamplePaidOrder() {
        Book sampleBookOne = givenSampleBookOne(50L);
        Long orderId = placeOrder(sampleBookOne.getId(), 15);
        manageOrderService.updateOrderStatus(orderId, OrderStatus.PAID);
        return orderId;
    }

}
