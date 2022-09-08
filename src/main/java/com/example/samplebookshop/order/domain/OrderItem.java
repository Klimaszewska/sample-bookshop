package com.example.samplebookshop.order.domain;

import com.example.samplebookshop.jpa.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    private Long bookId;
    private int quantity;
}
