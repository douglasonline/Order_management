package com.example.Order_management.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.OrderRequestRepository;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.OrderRequestItemService;
import com.example.Order_management.service.OrderRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar o banco de dados real
public class OrderRequestImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceRepository productServiceRepository;

    @MockBean
    private OrderRequestRepository orderRequestRepository;

    @MockBean
    private OrderRequestService orderRequestService;

    // Instância do ObjectMapper para serialização/deserialização de objetos JSON
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }


    @Test
    public void testCreateOrderRequest() {
        // Mock do ProductService
        UUID productServiceId = UUID.randomUUID();
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Mock do OrderRequest com o OrderRequestItem
        UUID orderRequestId = UUID.randomUUID();
        OrderRequest mockOrderRequest = new OrderRequest();
        mockOrderRequest.setId(orderRequestId);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));

        // Configurar o comportamento do serviço OrderRequestItemService mock
        OrderRequestItemService orderRequestItemService = mock(OrderRequestItemService.class);
        when(orderRequestItemService.create(orderRequestItem)).thenReturn(orderRequestItem);

        // Configurar o comportamento do repositório OrderRequestRepository mock
        when(orderRequestRepository.save(any(OrderRequest.class))).thenReturn(mockOrderRequest);

        // Configurar o comportamento do serviço OrderRequestItemService mock
        OrderRequestService orderRequestService = mock(OrderRequestService.class);
        when(orderRequestService.create(mockOrderRequest)).thenReturn(mockOrderRequest);

        // Chamar o método create
        OrderRequest createdOrderRequest = orderRequestService.create(mockOrderRequest);


        // Verificar se o pedido retornado não é nulo
        assertNotNull(createdOrderRequest);

        // Verificar se o totalAmount foi definido corretamente
        assertEquals(BigDecimal.valueOf(100.0), createdOrderRequest.getTotalAmount());

        // Verificar se os itens do pedido foram associados corretamente
        assertFalse(createdOrderRequest.getItems().isEmpty()); // Verificar se a lista de itens não está vazia

        // Imprimir os detalhes do pedido no console
        System.out.println("OrderRequest criado:");
        System.out.println("ID: " + createdOrderRequest.getId());
        System.out.println("Total Amount: " + createdOrderRequest.getTotalAmount());
        System.out.println("Is Open: " + createdOrderRequest.getIsOpen());
        System.out.println("OrderRequestItem associado:");
        for (OrderRequestItem item : createdOrderRequest.getItems()) {
            System.out.println("ID do Produto: " + item.getProductService().getId());
            System.out.println("Nome do Produto: " + item.getProductService().getName());
            System.out.println("Quantidade: " + item.getQuantity());
        }
    }

    @Test
    public void testApplyDiscount() {

        Double discountPercentage = 10.0;
        // Mock do ProductService
        UUID productServiceId = UUID.randomUUID();
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Mock do OrderRequest com o OrderRequestItem
        UUID orderRequestId = UUID.randomUUID();
        OrderRequest mockOrderRequest = new OrderRequest();
        mockOrderRequest.setId(orderRequestId);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));

        // Configurar o comportamento do serviço OrderRequestItemService mock
        OrderRequestItemService orderRequestItemService = mock(OrderRequestItemService.class);
        when(orderRequestItemService.create(orderRequestItem)).thenReturn(orderRequestItem);

        // Configurar o comportamento do repositório OrderRequestRepository mock
        when(orderRequestRepository.save(any(OrderRequest.class))).thenReturn(mockOrderRequest);


        // Simular que o OrderRequest existe no banco de dados
        Mockito.when(orderRequestRepository.findById(orderRequestId)).thenReturn(Optional.of(mockOrderRequest));

        // Simular a chamada do serviço applyDiscount
        Mockito.when(orderRequestService.applyDiscount(orderRequestId, discountPercentage)).thenReturn(Optional.of(mockOrderRequest));

        // Chamar o método applyDiscount
        Optional<OrderRequest> result = orderRequestService.applyDiscount(orderRequestId, discountPercentage);

        // Verificar se o resultado é presente
        assertTrue(result.isPresent());

        // Exibir os dados para o usuário no console
        System.out.println("OrderRequest com desconto aplicado:");
        System.out.println("ID: " + result.get().getId());
        System.out.println("Total Amount após desconto: " + result.get().getTotalAmount());
    }



    @Test
    public void testGetAllWithFilter() {
        // Mock do filtro
        String filtro = "produto";

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(UUID.randomUUID());
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Criar um OrderRequest mockado com o ProductService mockado
        OrderRequest orderRequest1 = new OrderRequest();
        orderRequest1.setId(UUID.randomUUID());
        orderRequest1.setTotalAmount(BigDecimal.valueOf(100.0));
        orderRequest1.setOpen(true);
        orderRequest1.setItems(Collections.singletonList(orderRequestItem));

        // Simulando a lista de pedidos retornada pelo serviço
        List<OrderRequest> mockOrderRequestList = new ArrayList<>();
        mockOrderRequestList.add(orderRequest1);

        // Configurando o comportamento do serviço mock
        Mockito.when(orderRequestService.getAllWithFilter(Mockito.eq(filtro)))
                .thenReturn(mockOrderRequestList);

        // Chamando a função que queremos testar
        List<OrderRequest> resultList = orderRequestService.getAllWithFilter(filtro);

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(orderRequestService).getAllWithFilter(Mockito.eq(filtro));

        // Imprimindo os detalhes dos pedidos na página no console
        System.out.println("Pedidos encontrados com filtro '" + filtro + "':");
        resultList.forEach(orderRequest -> {
            System.out.println("ID: " + orderRequest.getId());
            System.out.println("Total Amount: " + orderRequest.getTotalAmount());
            System.out.println("Is Open: " + orderRequest.getIsOpen());
            // Imprima outras propriedades conforme necessário
        });

        // Verificando se a lista retornada não é nula e contém os elementos esperados
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
        // Verificando se os elementos da lista têm os valores esperados
        OrderRequest resultOrderRequest = resultList.get(0);
        assertEquals(orderRequest1.getId(), resultOrderRequest.getId());
        assertEquals(orderRequest1.getTotalAmount(), resultOrderRequest.getTotalAmount());
        assertEquals(orderRequest1.getIsOpen(), resultOrderRequest.getIsOpen());
        // Verifique outras propriedades conforme necessário
    }





    @Test
    public void testGetAllWithFilterPaged() throws JsonProcessingException {
        // Mock do filtro
        String filtro = "produto";

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(UUID.randomUUID());
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Criar um OrderRequest mockado com o ProductService mockado
        OrderRequest orderRequest1 = new OrderRequest();
        orderRequest1.setId(UUID.randomUUID());
        orderRequest1.setTotalAmount(BigDecimal.valueOf(100.0));
        orderRequest1.setOpen(true);
        orderRequest1.setItems(Collections.singletonList(orderRequestItem));

        // Simulando a lista de pedidos retornada pelo serviço
        List<OrderRequest> mockOrderRequestList = new ArrayList<>();
        mockOrderRequestList.add(orderRequest1);

        // Criando uma página de exemplo
        Page<OrderRequest> mockPage = new PageImpl<>(mockOrderRequestList, PageRequest.of(0, 10), mockOrderRequestList.size());

        // Configurando o comportamento do serviço mock
        Mockito.when(orderRequestService.getAllWithFilter(Mockito.eq(filtro), Mockito.any()))
                .thenReturn(mockPage);

        // Chamando a função que queremos testar
        Page<OrderRequest> resultPage = orderRequestService.getAllWithFilter(filtro, PageRequest.of(0, 10));

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(orderRequestService).getAllWithFilter(Mockito.eq(filtro), Mockito.any());

        System.out.println();
        // Imprimindo os detalhes dos pedidos na página no console
        System.out.println("Pedidos encontrados com filtro '" + filtro + "':");
        resultPage.forEach(orderRequest -> {
            System.out.println("ID: " + orderRequest.getId());
            System.out.println("Total Amount: " + orderRequest.getTotalAmount());
            System.out.println("Is Open: " + orderRequest.getIsOpen());
        });

        // Serializando a página para JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String pageJson = objectMapper.writeValueAsString(mockPage);
        System.out.println("JSON da página:");
        System.out.println(pageJson);
    }




}

