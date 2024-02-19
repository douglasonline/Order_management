package com.example.Order_management.controller;

import com.example.Order_management.service.GenericService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class GenericController<T> {

    private final GenericService<T> service;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericController.class);

    @GetMapping("/all")
    public ResponseEntity<List<T>> getAllWithoutPagination() {
        List<T> items = service.getAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping
    public ResponseEntity<Page<T>> getAllWithPagination(Pageable pageable) {
        Page<T> items = service.getAll(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        T item = service.get(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        } else {
            Map<String, String> responseBody = Map.of("message", "ID " + id + " não foi encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody T item) {
        T updatedItem = service.update(id, item);
        if (updatedItem != null) {
            return ResponseEntity.ok(updatedItem);
        } else {
            Map<String, String> responseBody = Map.of("message", "ID " + id + " não foi encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            Optional<T> entityOptional = service.findById(id);

            if (entityOptional.isPresent()) {
                service.delete(id);
                LOGGER.info("EXCLUIR " + entityOptional.get().getClass().getSimpleName() + " POR ID: " + id);
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("Mensagem", "Id " + id + " excluído com sucesso!"));
            } else {
                LOGGER.error(entityOptional.get().getClass().getSimpleName() + " NÃO ENCONTRADO PARA EXCLUSÃO POR ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Mensagem", "Id: " + id + " não encontrado"));
            }
        } catch (Exception ex) {
            LOGGER.error("ERRO AO EXCLUIR " + ex.getClass().getSimpleName() + " POR ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Mensagem", "Erro ao excluir Id: " + id));
        }
    }


}
