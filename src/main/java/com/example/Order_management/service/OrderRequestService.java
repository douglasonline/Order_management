package com.example.Order_management.service;

import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrderRequestService extends GenericService<OrderRequest>{

    Page<OrderRequest> getAll(Pageable pageable);

    // Método utilizando QueryDSL para salvar um registro
    OrderRequest create(OrderRequest orderRequest);

    // Método para aplicar desconto em um pedido
    Optional<OrderRequest> applyDiscount(UUID orderId, double discountPercentage);

    List<OrderRequest> getAllWithFilter(String filtro);

    Page<OrderRequest> getAllWithFilter(String filtro, Pageable pageable);

}
