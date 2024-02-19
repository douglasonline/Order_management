package com.example.Order_management.controller;

import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.service.ProductServiceService;
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
@RequestMapping("api/productService")
public class ProductServiceController extends GenericController<ProductService> {

    @Autowired
    ProductServiceService productServiceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceController.class);

    public ProductServiceController(ProductServiceService productServiceService) {
        super(productServiceService);
    }

    @PostMapping
    public ResponseEntity<ProductService> create(@RequestBody ProductService productService) {
        ProductService newItem = productServiceService.create(productService);
        return ResponseEntity.ok(newItem);
    }

    @GetMapping("/allWithFilter")
    public ResponseEntity<?> getAllWithFilter(@RequestParam(required = false) String filtro) {
        if (filtro != null && !filtro.isEmpty()) {
            List<ProductService> items = productServiceService.getAllWithFilter(filtro);
            return ResponseEntity.ok(items);
        } else {
            return ResponseEntity.badRequest().body("O parâmetro 'filtro' é obrigatório.");
        }
    }

    @GetMapping("/allWithPagination")
    public ResponseEntity<Page<ProductService>> getAllWithPaginationWithFilter(@RequestParam(required = false) String filtro, Pageable pageable) {
        Page<ProductService> items;
        if (filtro != null && !filtro.isEmpty()) {
            items = productServiceService.getAllWithFilter(filtro, pageable);
        } else {
            items = productServiceService.getAll(pageable);
        }
        return ResponseEntity.ok(items);
    }



}