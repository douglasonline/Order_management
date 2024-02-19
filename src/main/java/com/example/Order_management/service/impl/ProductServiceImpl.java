package com.example.Order_management.service.impl;

import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.model.QOrderRequestItem;
import com.example.Order_management.model.QProductService;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.ProductServiceService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl extends GenericServiceImpl<ProductService, UUID, ProductServiceRepository> implements ProductServiceService {

    @Autowired
    JPAQueryFactory queryFactory;

    public ProductServiceImpl(ProductServiceRepository repository) {
        super(repository);
    }

    @Override
    public ProductService create(ProductService productOrService) {

        return repository.save(productOrService);

    }

    @Override
    public List<ProductService> getAllWithFilter(String filtro) {
        // Construir expressão com QueryDSL
        String filtroLowerCase = filtro.toLowerCase();
        BooleanExpression expression = QProductService.productService.name.lower().like("%" + filtroLowerCase + "%");
        Iterable<ProductService> iterable = repository.findAll(expression);
        // Converter Iterable para List
        List<ProductService> resultList = new ArrayList<>();
        iterable.forEach(resultList::add);
        return resultList;
    }

    @Override
    public Page<ProductService> getAllWithFilter(String filtro, Pageable pageable) {
        // Construir expressão com QueryDSL
        String filtroLowerCase = filtro.toLowerCase();
        BooleanExpression expression = QProductService.productService.name.lower().like("%" + filtroLowerCase + "%");
        return repository.findAll(expression, pageable);
    }





}
