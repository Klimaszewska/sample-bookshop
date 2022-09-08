package com.example.samplebookshop.order.domain;

import com.example.samplebookshop.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipient extends BaseEntity {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
