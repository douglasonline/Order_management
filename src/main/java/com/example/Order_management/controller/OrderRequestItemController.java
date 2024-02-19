package com.example.Order_management.controller;

import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.OrderRequestItemService;
import com.example.Order_management.service.OrderRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/orderRequestItem")
public class OrderRequestItemController extends GenericController<OrderRequestItem> {

    @Autowired
    OrderRequestItemService orderRequestItemService;
    @Autowired
    ProductServiceRepository productServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRequestItemController.class);

    public OrderRequestItemController(OrderRequestItemService orderRequestItemService) {

        super(orderRequestItemService);

    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequestItem orderRequestItem) {
        try {
            // Busca o ProductService correspondente no banco de dados
            ProductService productService = productServiceRepository.findById(orderRequestItem.getProductService().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + orderRequestItem.getProductService().getId()));

            // Verifica se os dados do ProductService estão completos
            if (productService.getName() == null || productService.getPrice() == null) {
                throw new RuntimeException("Dados incompletos para o ProductService com ID: " + productService.getId());
            }

            // Atualiza o ProductService no item com o ProductService recuperado do banco de dados
            orderRequestItem.setProductService(productService);

            // Cria o OrderRequestItem com os dados atualizados do ProductService
            OrderRequestItem newItem = orderRequestItemService.create(orderRequestItem);

            return ResponseEntity.ok(newItem);
        } catch (RuntimeException e) {
            // Log da exceção ou tratamento adequado aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar item do pedido: " + e.getMessage());
        }
    }



    @GetMapping("/allWithFilter")
    public ResponseEntity<?> getAllWithFilter(@RequestParam(required = false) String filtro) {
        if (filtro != null && !filtro.isEmpty()) {
            List<OrderRequestItem> items = orderRequestItemService.getAllWithFilter(filtro);
            return ResponseEntity.ok(items);
        } else {
            return ResponseEntity.badRequest().body("O parâmetro 'filtro' é obrigatório.");
        }
    }

    @GetMapping("/allWithPagination")
    public ResponseEntity<?> getAllWithPaginationWithFilter(@RequestParam(required = false) String filtro, Pageable pageable) {
        if (filtro != null && !filtro.isEmpty()) {
            Page<OrderRequestItem> items = orderRequestItemService.getAllWithFilter(filtro, pageable);
            return ResponseEntity.ok(items);
        } else {
            return ResponseEntity.badRequest().body("O parâmetro 'filtro' é obrigatório.");
        }
    }


}
