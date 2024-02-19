package com.example.Order_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@QueryEntity
@Getter
@Setter
@Table(name = "order_request_item")
public class OrderRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "orderRequest_id")
    @JsonBackReference // Indica que esta é a referência de retorno (backward)
    private OrderRequest orderRequest;

    @NotNull
    @ManyToOne
    private ProductService productService;

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(nullable = false)
    private int quantity;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", orderRequest=" + (orderRequest != null ? orderRequest.getId() : null) +
                ", productService=" + (productService != null ? productService.getName() : null) +
                ", quantity=" + quantity +
                '}';
    }


}