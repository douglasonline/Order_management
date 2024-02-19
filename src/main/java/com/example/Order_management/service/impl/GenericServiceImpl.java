package com.example.Order_management.service.impl;

import com.example.Order_management.service.GenericService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


public class GenericServiceImpl<T, ID extends UUID, R extends JpaRepository<T, ID> & QuerydslPredicateExecutor<T>> implements GenericService<T> {

    protected final R repository;

    public GenericServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public Page<T> getAll(Pageable pageable) {

        return repository.findAll(pageable);

    }

    @Override
    public List<T> getAll() {

        return repository.findAll();

    }

    @Override
    public T get(UUID id) {
        Optional<T> optionalItem = repository.findById((ID) id);
        return optionalItem.orElseThrow(() -> new NoSuchElementException("Id: " + id));
    }

    @Override
    public T update(UUID id, T item) {

        if (repository.existsById((ID) id)) {
            return repository.save(item);
        } else {
            return null;
        }

    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById((ID) id)) {
            throw new EmptyResultDataAccessException(1);
        }
        repository.deleteById((ID) id);
    }

    @Override
    public Optional<T> findById(UUID id) {
        return repository.findById((ID) id);
    }

}