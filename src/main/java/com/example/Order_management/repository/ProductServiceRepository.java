package com.example.Order_management.repository;

import com.example.Order_management.model.ProductService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductServiceRepository extends JpaRepository<ProductService, UUID>, QuerydslPredicateExecutor<ProductService> {
}
