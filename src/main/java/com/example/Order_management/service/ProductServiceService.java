package com.example.Order_management.service;


import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductServiceService extends GenericService<ProductService> {

    Page<ProductService> getAll(Pageable pageable);

    // Método utilizando QueryDSL para buscar todos os registros com filtro
    List<ProductService> getAllWithFilter(String filtro);

    // Método utilizando QueryDSL para buscar todos os registros com filtro e paginação
    Page<ProductService> getAllWithFilter(String filtro, Pageable pageable);

    // Método utilizando QueryDSL para salvar um registro
    ProductService create(ProductService productOrService);



}
