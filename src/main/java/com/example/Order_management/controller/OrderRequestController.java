package com.example.Order_management.controller;

import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.OrderRequestService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@Transactional
@RequestMapping("api/orderRequest")
public class OrderRequestController extends GenericController<OrderRequest> {

    @Autowired
    OrderRequestService orderRequestService;
    @Autowired
    ProductServiceRepository productServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRequestController.class);

    public OrderRequestController(OrderRequestService OrderRequestService) {

        super(OrderRequestService);

    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequest orderRequest) {
        try {
            // Para cada item no OrderRequest, verifique se o ProductService correspondente está no banco de dados
            for (OrderRequestItem item : orderRequest.getItems()) {
                ProductService productService = productServiceRepository.findById(item.getProductService().getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + item.getProductService().getId()));

                // Atualize o ProductService no item com o ProductService recuperado do banco de dados
                item.setProductService(productService);
            }

            // Crie o OrderRequest com os dados atualizados dos ProductServices
            OrderRequest newItem = orderRequestService.create(orderRequest);

            return ResponseEntity.ok(newItem);
        } catch (RuntimeException e) {
            // Log da exceção ou tratamento adequado aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar pedido: " + e.getMessage());
        }
    }


    @PostMapping("/{orderRequestId}/apply-discount")
    public ResponseEntity<?> applyDiscount(@PathVariable UUID orderRequestId, @RequestBody Map<String, Double> requestBody) {
        try {
            // Extrair o percentual de desconto do corpo da solicitação
            Double discountPercentage = requestBody.get("discountPercentage");

            // Verificar se o desconto foi fornecido
            if (discountPercentage == null) {
                throw new IllegalArgumentException("O percentual de desconto não foi fornecido.");
            }

            // Aplicar o desconto no pedido
            Optional<OrderRequest> updatedOrderRequestOptional = orderRequestService.applyDiscount(orderRequestId, discountPercentage);

            if (updatedOrderRequestOptional.isPresent()) {
                return ResponseEntity.ok(updatedOrderRequestOptional.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao aplicar desconto no pedido.");
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderRequest>> getAllWithFilter(@RequestParam(required = false) String filtro) {
        List<OrderRequest> orderRequests;
        if (filtro != null && !filtro.isEmpty()) {
            orderRequests = orderRequestService.getAllWithFilter(filtro);
        } else {
            // Se o filtro não for fornecido, retorna todos os itens
            orderRequests = orderRequestService.getAll();
        }
        return ResponseEntity.ok(orderRequests);
    }

    @GetMapping("/filter-page")
    public ResponseEntity<Page<OrderRequest>> getAllWithFilterPaged(@RequestParam(required = false) String filtro, Pageable pageable) {
        Page<OrderRequest> orderRequests;
        if (filtro != null && !filtro.isEmpty()) {
            orderRequests = orderRequestService.getAllWithFilter(filtro, pageable);
        } else {
            // Se o filtro não for fornecido, retorna todos os itens com paginação
            orderRequests = orderRequestService.getAll(pageable);
        }
        return ResponseEntity.ok(orderRequests);
    }

}
