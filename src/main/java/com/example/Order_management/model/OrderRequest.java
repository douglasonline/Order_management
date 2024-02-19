package com.example.Order_management.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@QueryEntity
@Table(name = "order_request")
public class OrderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("isOpen")
    private boolean isOpen;
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "orderRequest", cascade = CascadeType.ALL)
    @JsonManagedReference // Indica que esta é a referência gerenciada (forward)
    private List<OrderRequestItem> items;

    public boolean getIsOpen() {
        return isOpen;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", totalAmount=" + totalAmount +
                ", isOpen=" + isOpen +
                // Adicione outros campos que deseja incluir na representação do toString()
                '}';
    }
}