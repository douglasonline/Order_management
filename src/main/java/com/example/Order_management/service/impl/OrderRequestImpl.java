package com.example.Order_management.service.impl;

import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.exception.OrderRequestNotFoundException;
import com.example.Order_management.repository.OrderRequestItemRepository;
import com.example.Order_management.repository.OrderRequestRepository;
import com.example.Order_management.service.OrderRequestService;
import com.example.Order_management.model.QOrderRequestItem;
import com.example.Order_management.model.QOrderRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional
public class OrderRequestImpl extends GenericServiceImpl<OrderRequest, UUID, OrderRequestRepository> implements OrderRequestService {

    @Autowired
    OrderRequestRepository orderRequestRepository;
    @Autowired
    OrderRequestItemRepository orderRequestItemRepository;
    @Autowired
    JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestImpl.class);

    public OrderRequestImpl(OrderRequestRepository repository) {

        super(repository);

    }

    @Override
    @Transactional
    public OrderRequest create(OrderRequest orderRequest) {
        try {
            // Calcular o totalAmount a partir dos itens do pedido
            BigDecimal totalAmount = BigDecimal.ZERO;
            if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
                for (OrderRequestItem orderRequestItem : orderRequest.getItems()) {
                    BigDecimal itemPrice = orderRequestItem.getProductService().getPrice();
                    BigDecimal itemQuantity = BigDecimal.valueOf(orderRequestItem.getQuantity());
                    BigDecimal itemTotal = itemPrice.multiply(itemQuantity);
                    totalAmount = totalAmount.add(itemTotal);
                }
            }

            // Definir o totalAmount calculado
            orderRequest.setTotalAmount(totalAmount);

            // Salvar o pedido para obter o ID atribuído
            OrderRequest savedOrderRequest = orderRequestRepository.save(orderRequest);

            // Verifica se a lista de itens não é nula e não está vazia
            if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
                // Construir expressão com QueryDSL para verificar se todos os produtos associados ao pedido estão ativos
                QOrderRequestItem qOrderRequestItem = QOrderRequestItem.orderRequestItem;
                for (OrderRequestItem orderRequestItem : savedOrderRequest.getItems()) {
                    // Verifica se o item do pedido é um produto ou um serviço
                    if (!orderRequestItem.getProductService().isService()) {
                        // Se for um produto, verifica se está ativo
                        BooleanExpression predicate = qOrderRequestItem.productService.isActive.isTrue()
                                .and(qOrderRequestItem.productService.eq(orderRequestItem.getProductService()));

                        // Executa a consulta com o predicado
                        boolean productIsActive = queryFactory.select(qOrderRequestItem)
                                .from(qOrderRequestItem)
                                .where(predicate)
                                .fetchFirst() != null;

                        // Verifica se o produto associado ao item do pedido está ativo
                        if (!productIsActive) {
                            // Exclui o pedido e seus itens do banco de dados
                            orderRequestRepository.delete(savedOrderRequest);
                            throw new IllegalArgumentException("Não é possível adicionar um produto desativado em um pedido.");
                        }
                    }
                }
            }

            // Associar o ID do pedido aos itens do pedido
            if (orderRequest.getItems() != null) {
                for (OrderRequestItem orderRequestItem : savedOrderRequest.getItems()) {
                    orderRequestItem.setOrderRequest(savedOrderRequest);
                }
            }

            // Salvar os itens do pedido associados ao pedido
            for (OrderRequestItem orderRequestItem : savedOrderRequest.getItems()) {
                orderRequestItemRepository.save(orderRequestItem);
            }

            return savedOrderRequest;
        } catch (IllegalArgumentException e) {
            // Log da exceção ou tratamento adequado aqui
            throw e;
        } catch (Exception e) {
            // Log da exceção ou tratamento adequado aqui
            throw new RuntimeException("Erro ao criar pedido", e);
        }
    }



    @Override
    public Optional<OrderRequest> applyDiscount(UUID orderRequestId, double discountPercentage) {
        QOrderRequest qOrderRequest = QOrderRequest.orderRequest;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        OrderRequest orderRequest = queryFactory.selectFrom(qOrderRequest)
                .where(qOrderRequest.id.eq(orderRequestId))
                .fetchOne();

        if (orderRequest == null) {
            throw new OrderRequestNotFoundException("Pedido não encontrado.");
        }

        if (!orderRequest.getIsOpen()) {
            throw new IllegalStateException("Não é possível aplicar desconto em um pedido fechado.");
        }

        // Valor original
        BigDecimal originalAmount = orderRequest.getTotalAmount();

        // Convertendo a porcentagem de desconto em decimal
        BigDecimal discountPercentageDecimal = BigDecimal.valueOf(discountPercentage / 100);

        // Calcula o valor do desconto
        BigDecimal discountValue = originalAmount.multiply(discountPercentageDecimal);

        // Calcula o novo valor com desconto
        BigDecimal totalAmountWithDiscount = originalAmount.subtract(discountValue);

        // Arredonda o valor total com desconto para duas casas decimais
        totalAmountWithDiscount = totalAmountWithDiscount.setScale(2, RoundingMode.HALF_UP);

        // Atualiza o valor total do pedido com o desconto aplicado
        orderRequest.setTotalAmount(totalAmountWithDiscount);

        // Salva as alterações
        entityManager.merge(orderRequest);

        return Optional.of(orderRequest);

    }

    @Override
    public List<OrderRequest> getAllWithFilter(String filtro) {
        QOrderRequest qOrderRequest = QOrderRequest.orderRequest;
        String filtroLowerCase = filtro.toLowerCase();
        BooleanExpression expression = qOrderRequest.items.any().productService.name.lower().like("%" + filtroLowerCase + "%");
        return new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequest)
                .where(expression)
                .fetch();
    }

    @Override
    public Page<OrderRequest> getAllWithFilter(String filtro, Pageable pageable) {
        QOrderRequest qOrderRequest = QOrderRequest.orderRequest;
        String filtroLowerCase = filtro.toLowerCase();
        BooleanExpression expression = qOrderRequest.items.any().productService.name.lower().like("%" + filtroLowerCase + "%");
        List<OrderRequest> result = new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequest)
                .where(expression)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long count = new JPAQueryFactory(entityManager)
                .selectFrom(qOrderRequest)
                .where(expression)
                .fetchCount();
        return new PageImpl<>(result, pageable, count);
    }



}
