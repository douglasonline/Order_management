package com.example.Order_management.service.impl;

import com.example.Order_management.service.GenericService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.beans.BeanUtils;


import java.beans.PropertyDescriptor;
import java.util.*;


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

    public T update(UUID id, T newItem) {
        // Verifique se o item existe no banco de dados
        Optional<T> existingItemOptional = repository.findById((ID) id);
        if (existingItemOptional.isPresent()) {
            T existingItem = existingItemOptional.get();

            // Obtenha todas as propriedades do newItem que não são nulas
            BeanWrapper srcBeanWrapper = new BeanWrapperImpl(newItem);
            PropertyDescriptor[] propertyDescriptors = srcBeanWrapper.getPropertyDescriptors();
            Set<String> nullPropertyNames = new HashSet<>();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (srcBeanWrapper.getPropertyValue(propertyName) == null) {
                    nullPropertyNames.add(propertyName);
                }
            }

            // Copie as propriedades do newItem para o existingItem, excluindo aquelas que são nulas
            BeanUtils.copyProperties(newItem, existingItem, nullPropertyNames.toArray(new String[0]));

            // Salve o existingItem atualizado no repositório
            T updatedItem = repository.save(existingItem);
            return updatedItem;
        } else {
            // Se o item não existe, retorne null
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