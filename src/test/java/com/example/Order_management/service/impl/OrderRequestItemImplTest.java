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
import com.example.Order_management.repository.OrderRequestItemRepository;
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




@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar o banco de dados real
public class OrderRequestItemImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceRepository productServiceRepository;

    @MockBean
    private OrderRequestItemRepository orderRequestItemRepository;

    @MockBean
    private OrderRequestItemService orderRequestItemService;

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
        UUID orderRequestId = UUID.randomUUID();
        UUID item1Id = UUID.randomUUID(); // ID do primeiro item do pedido
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setId(item1Id); // Definindo o ID do item
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Mock do OrderRequest com o OrderRequestItem
        OrderRequest mockOrderRequest = new OrderRequest();
        mockOrderRequest.setId(orderRequestId);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));

        // Mock do serviço para retornar o objeto OrderRequestItem criado
        OrderRequestItemService orderRequestItemService = mock(OrderRequestItemService.class);
        when(orderRequestItemService.create(orderRequestItem)).thenReturn(orderRequestItem);

        // Chama o método create do OrderRequestItemService
        OrderRequestItem createdOrderRequestItem = orderRequestItemService.create(orderRequestItem);

        System.out.println();
        // Exibir os dados no console
        System.out.println("OrderRequest criado:");
        System.out.println("ID: " + mockOrderRequest.getId());
        System.out.println("Total Amount: " + mockOrderRequest.getTotalAmount());
        System.out.println("Is Open: " + mockOrderRequest.getIsOpen());
        System.out.println("OrderRequestItem associado:");
        System.out.println("ID: " + createdOrderRequestItem.getId());
        System.out.println("Quantity: " + createdOrderRequestItem.getQuantity());
        // Exibir outras propriedades conforme necessário

        // Verificar se o OrderRequestItem criado não é nulo
        assertNotNull(createdOrderRequestItem);
        // Verificar se os IDs correspondem
        assertEquals(item1Id, createdOrderRequestItem.getId());
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

        // Criar um OrderRequestItem mockado com o ProductService mockado
        OrderRequestItem orderRequestItem1 = new OrderRequestItem();
        orderRequestItem1.setId(UUID.randomUUID());
        orderRequestItem1.setProductService(mockProductService);
        orderRequestItem1.setQuantity(2);

        // Simulando a lista de itens de pedido retornada pelo serviço
        List<OrderRequestItem> mockOrderRequestItemList = new ArrayList<>();
        mockOrderRequestItemList.add(orderRequestItem1);

        // Configurando o comportamento do serviço mock
        Mockito.when(orderRequestItemService.getAllWithFilter(Mockito.eq(filtro)))
                .thenReturn(mockOrderRequestItemList);

        // Chamando a função que queremos testar
        List<OrderRequestItem> resultList = orderRequestItemService.getAllWithFilter(filtro);

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(orderRequestItemService).getAllWithFilter(Mockito.eq(filtro));

        System.out.println();
        // Imprimindo os detalhes dos itens de pedido no console
        System.out.println("Itens de pedido encontrados com filtro '" + filtro + "':");
        resultList.forEach(orderRequestItem -> {
            System.out.println("ID: " + orderRequestItem.getId());
            System.out.println("Product Service ID: " + orderRequestItem.getProductService().getId());
            System.out.println("Quantity: " + orderRequestItem.getQuantity());
            // Imprima outras propriedades conforme necessário
        });

        // Verificando se a lista retornada não é nula e contém os elementos esperados
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
        // Verificando se os elementos da lista têm os valores esperados
        OrderRequestItem resultOrderRequestItem = resultList.get(0);
        assertEquals(orderRequestItem1.getId(), resultOrderRequestItem.getId());
        assertEquals(orderRequestItem1.getProductService().getId(), resultOrderRequestItem.getProductService().getId());
        assertEquals(orderRequestItem1.getQuantity(), resultOrderRequestItem.getQuantity());
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

        // Criar um OrderRequestItem mockado com o ProductService mockado
        OrderRequestItem orderRequestItem1 = new OrderRequestItem();
        orderRequestItem1.setId(UUID.randomUUID());
        orderRequestItem1.setProductService(mockProductService);
        orderRequestItem1.setQuantity(2);

        // Simulando a lista de itens de pedido retornada pelo serviço
        List<OrderRequestItem> mockOrderRequestItemList = Collections.singletonList(orderRequestItem1);

        // Criando uma página de exemplo
        Page<OrderRequestItem> mockPage = new PageImpl<>(mockOrderRequestItemList, PageRequest.of(0, 10), mockOrderRequestItemList.size());

        // Configurando o comportamento do serviço mock
        Mockito.when(orderRequestItemService.getAllWithFilter(Mockito.eq(filtro), Mockito.any()))
                .thenReturn(mockPage);

        // Chamando a função que queremos testar
        Page<OrderRequestItem> resultPage = orderRequestItemService.getAllWithFilter(filtro, PageRequest.of(0, 10));

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(orderRequestItemService).getAllWithFilter(Mockito.eq(filtro), Mockito.any());

        System.out.println();
        // Imprimindo os detalhes dos itens de pedido na página no console
        System.out.println("Itens de pedido encontrados com filtro '" + filtro + "' (Página " + (resultPage.getNumber() + 1) + "):");
        resultPage.forEach(orderRequestItem -> {
            System.out.println("ID: " + orderRequestItem.getId());
            System.out.println("Product Service ID: " + orderRequestItem.getProductService().getId());
            System.out.println("Quantity: " + orderRequestItem.getQuantity());
            // Imprima outras propriedades conforme necessário
        });

        // Imprimindo o JSON da página no console
        ObjectMapper objectMapper = new ObjectMapper();
        String pageJson = objectMapper.writeValueAsString(mockPage);
        System.out.println("JSON da página:");
        System.out.println(pageJson);

        // Verificando se a página retornada não é nula e contém os elementos esperados
        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getContent().size()); // Verifique se o conteúdo da página está correto
        // Verifique outras propriedades conforme necessário
    }




}


