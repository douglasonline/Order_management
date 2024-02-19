package com.example.Order_management.service.impl;

import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.QOrderRequestItem;
import com.example.Order_management.repository.OrderRequestItemRepository;
import com.example.Order_management.service.OrderRequestItemService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderRequestItemImpl extends GenericServiceImpl<OrderRequestItem, UUID, OrderRequestItemRepository> implements OrderRequestItemService {

    @Autowired
    OrderRequestItemRepository orderRequestItemRepository;

    @Autowired
    JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public OrderRequestItemImpl(OrderRequestItemRepository repository) {

        super(repository);

    }


    @Override
    public OrderRequestItem create(OrderRequestItem orderRequestItem) {

        return orderRequestItemRepository.save(orderRequestItem);

    }



    @Override
    public List<OrderRequestItem> getAllWithFilter(String filtro) {
        QOrderRequestItem qOrderRequestItem = QOrderRequestItem.orderRequestItem;
        BooleanExpression expression = qOrderRequestItem.productService.name.containsIgnoreCase(filtro);
        return new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequestItem)
                .where(expression)
                .fetch();
    }

    @Override
    public Page<OrderRequestItem> getAllWithFilter(String filtro, Pageable pageable) {
        QOrderRequestItem qOrderRequestItem = QOrderRequestItem.orderRequestItem;
        BooleanExpression expression = qOrderRequestItem.productService.name.containsIgnoreCase(filtro);
        List<OrderRequestItem> result = new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequestItem)
                .where(expression)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long count = new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequestItem)
                .where(expression)
                .fetchCount();
        return new PageImpl<>(result, pageable, count);
    }


}
