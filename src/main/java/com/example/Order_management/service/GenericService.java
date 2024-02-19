package com.example.Order_management.service;

import com.example.Order_management.model.OrderRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenericService<T> {

    // Método utilizando QueryDSL para buscar todos os registros com paginação
    Page<T> getAll(Pageable pageable);

    // Método utilizando QueryDSL para buscar todos os registros
    List<T> getAll();

    // Método utilizando QueryDSL para buscar um registro por ID
    T get(UUID id);

    // Método utilizando QueryDSL para atualizar um registro por ID
    T update(UUID id, T item);

    Optional<T> findById(UUID id);

    void delete(UUID id);


}
