package com.example.Order_management.service;

import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrderRequestItemService extends GenericService<OrderRequestItem>{

    OrderRequestItem create(OrderRequestItem orderRequestItem);

    List<OrderRequestItem> getAllWithFilter(String filtro);

    Page<OrderRequestItem> getAllWithFilter(String filtro, Pageable pageable);


}
