package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.clock.Clock;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.order.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Duration;

import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "app.order.payment-period=1H")
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock(){
            return new Clock.Fake();
        }
    }

    @Autowired
    private AbandonedOrdersJob ordersJob;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private ManageOrderService manageOrderService;

    @Autowired
    private QueryOrderUseCase queryOrderUseCase;

    @Autowired
    private CatalogUseCase catalogUseCase;

    @Autowired
    private Clock.Fake clock;


    @Test
    void shouldMarkOrdersAsAbandoned() {
        // given
        Book book = givenSampleBookOne(50);
        Long orderId = placeOrder(book.getId(), 15);

        //when
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        //then
        assertEquals(OrderStatus.ABANDONED, queryOrderUseCase.findOneById(orderId).get().getStatus());
        assertEquals(50L, getAvailableBooks(book));
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

    private Recipient createRecipient() {
        return Recipient.builder().email("first.user@example.org").build();
    }

    private Book givenSampleBookOne(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Long getAvailableBooks(Book sampleBookOne) {
        return catalogUseCase.findOneById(sampleBookOne.getId()).get().getAvailableBooks();
    }
}
