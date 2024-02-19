package com.example.Order_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.controller.OrderRequestItemController;
import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.OrderRequestItemService;
import com.example.Order_management.service.ProductServiceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@AutoConfigureMockMvc
public class OrderRequestItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceService productServiceService;

    @MockBean
    private OrderRequestItemService orderRequestItemService;

    @MockBean
    private ProductServiceRepository productServiceRepository;

    @Autowired
    private OrderRequestItemController orderRequestItemController;


    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void create_OrderRequestItemValid_ReturnsOkResponse() {
        // Arrange
        OrderRequestItem mockOrderRequestItem = new OrderRequestItem();
        mockOrderRequestItem.setId(UUID.randomUUID());
        mockOrderRequestItem.setQuantity(2);

        // Criar um OrderRequest
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setId(UUID.randomUUID()); // Defina um ID para o OrderRequest

        // Definir o OrderRequest no mockOrderRequestItem
        mockOrderRequestItem.setOrderRequest(orderRequest);

        ProductService productService = new ProductService();
        productService.setId(UUID.randomUUID());
        productService.setName("Produto 1");
        productService.setPrice(BigDecimal.valueOf(50.0));

        mockOrderRequestItem.setProductService(productService);

        when(orderRequestItemService.create(mockOrderRequestItem)).thenReturn(mockOrderRequestItem);
        when(productServiceRepository.findById(productService.getId())).thenReturn(Optional.of(productService));

        // Act
        ResponseEntity<?> response = orderRequestItemController.create(mockOrderRequestItem);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockOrderRequestItem, response.getBody());

        // Log dos dados retornados no console
        System.out.println("Dados retornados:");
        System.out.println(response.getBody());

    }


    @Test
    void getAllWithFilter_FilterProvided_ReturnsOkResponse() {
        // Arrange
        UUID productServiceId = UUID.randomUUID();
        UUID orderRequestItemId = UUID.randomUUID(); // Novo UUID para o item

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setId(orderRequestItemId); // Definindo o ID do item
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Mock da lista de OrderRequestItem
        List<OrderRequestItem> orderRequestItems = Collections.singletonList(orderRequestItem);

        // Configurando o comportamento do serviço para retornar a lista de OrderRequestItem
        when(orderRequestItemService.getAllWithFilter(anyString())).thenReturn(orderRequestItems);

        // Act
        ResponseEntity<?> response = orderRequestItemController.getAllWithFilter("filtro");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderRequestItems, response.getBody());

        // Log dos dados retornados no console
        System.out.println("Dados retornados:");
        System.out.println(response.getBody());
    }



    @Test
    void getAllWithPaginationWithFilter_FilterProvided_ReturnsOkResponse() throws Exception {
        // Arrange
        UUID productServiceId = UUID.randomUUID();
        UUID orderRequestItemId1 = UUID.randomUUID();
        UUID orderRequestItemId2 = UUID.randomUUID();

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));

        // Mock dos OrderRequestItems com o ProductService mockado
        OrderRequestItem orderRequestItem1 = new OrderRequestItem();
        orderRequestItem1.setId(orderRequestItemId1);
        orderRequestItem1.setProductService(mockProductService);
        orderRequestItem1.setQuantity(2);

        OrderRequestItem orderRequestItem2 = new OrderRequestItem();
        orderRequestItem2.setId(orderRequestItemId2);
        orderRequestItem2.setProductService(mockProductService);
        orderRequestItem2.setQuantity(3);

        List<OrderRequestItem> orderRequestItems = Arrays.asList(orderRequestItem1, orderRequestItem2);
        PageImpl<OrderRequestItem> orderRequestItemPage = new PageImpl<>(orderRequestItems, PageRequest.of(0, 10), orderRequestItems.size());

        // Simulando a resposta do serviço
        Page<OrderRequestItem> orderRequestItemsPage = new PageImpl<>(orderRequestItems);

        // Mock do serviço de OrderRequestItem
        Mockito.when(orderRequestItemService.getAllWithFilter(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(orderRequestItemsPage);

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/orderRequestItem/allWithPagination")
                        .param("filtro", "produto") // Removido o valor UUID e mantido como string
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        // Extrair e imprimir os dados retornados
        String content = result.getResponse().getContentAsString();
        System.out.println("Dados retornados:");
        System.out.println(content);

    }



}