package com.example.samplebookshop.order.application.port;

import com.example.samplebookshop.order.application.RichOrder;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<RichOrder> findAll();

    Optional<RichOrder> findOneById(Long id);

}
