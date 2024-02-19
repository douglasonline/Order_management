package com.example.Order_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.controller.OrderRequestController;
import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.OrderRequestRepository;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.OrderRequestService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar o banco de dados real
public class OrderRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceRepository productServiceRepository;

    @MockBean
    private OrderRequestRepository orderRequestRepository;

    @MockBean
    private OrderRequestController orderRequestController;

    // Instância do ObjectMapper para serialização/deserialização de objetos JSON
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }


    @Test
    public void testCreateOrderRequest() throws Exception {
        // Simulando um ID válido para o ProductService e para os itens do pedido
        UUID existingProductId = UUID.randomUUID();
        UUID item1Id = UUID.randomUUID(); // ID do primeiro item do pedido

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(existingProductId);
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
        mockOrderRequest.setId(UUID.randomUUID()); // ID gerado aleatoriamente para o pedido
        mockOrderRequest.setTotalAmount(null); // Valor será calculado no serviço
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));

        // Simulando a existência do ProductService no banco de dados
        when(productServiceRepository.findById(existingProductId)).thenReturn(Optional.of(mockProductService));

        // Simulando a criação de um pedido
        when(orderRequestController.create(any(OrderRequest.class)))
                .thenAnswer(invocation -> {
                    OrderRequest createdOrderRequest = invocation.getArgument(0); // Obtém o OrderRequest passado para o método create

                    // Para cada item no OrderRequest, verifique se o ProductService correspondente está no banco de dados
                    for (OrderRequestItem item : createdOrderRequest.getItems()) {
                        ProductService productService = productServiceRepository.findById(item.getProductService().getId())
                                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + item.getProductService().getId()));

                        // Atualize o ProductService no item com o ProductService recuperado do banco de dados
                        item.setProductService(productService);
                    }

                    // Simula a criação do OrderRequest com os dados atualizados dos ProductServices
                    return ResponseEntity.ok(createdOrderRequest);
                });

        // Chamada ao endpoint para criar um pedido
        mockMvc.perform(post("/api/orderRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Verifica se o ID do pedido foi retornado na resposta
                .andExpect(jsonPath("$.totalAmount").value(100.0)) // Verifica se o totalAmount está correto
                .andExpect(jsonPath("$.isOpen").value(true)) // Verifica se isOpen está como true
                .andExpect(jsonPath("$.items").isArray()) // Verifica se a lista de items está presente
                .andExpect(jsonPath("$.items", hasSize(1))) // Verifica se há apenas um item na lista
                .andExpect(jsonPath("$.items[0].id").value(item1Id.toString())) // Verifica se o ID do item está correto
                .andExpect(jsonPath("$.items[0].productService.id").value(existingProductId.toString())) // Verifica se o ID do ProductService está correto
                .andExpect(jsonPath("$.items[0].productService.name").value("Produto 1")) // Verifica se o nome do ProductService está correto
                .andExpect(jsonPath("$.items[0].productService.price").value(50.0)) // Verifica se o preço do ProductService está correto
                .andExpect(jsonPath("$.items[0].quantity").value(2)) // Verifica se a quantidade do item está correta
                .andDo(result -> {
                    // Extrai o corpo da resposta e o imprime no console
                    String content = result.getResponse().getContentAsString();
                    System.out.println("Detalhes do Pedido:");
                    System.out.println(content);
                });

        // Verifica se o pedido foi criado corretamente
        verify(orderRequestController, times(1)).create(any(OrderRequest.class));
    }


    @Test
    public void testApplyDiscount() throws Exception {
        UUID orderRequestId = UUID.randomUUID();
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
        OrderRequest mockOrderRequest = new OrderRequest();
        mockOrderRequest.setId(orderRequestId);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));

        // Simular que o OrderRequest existe no banco de dados
        Mockito.when(orderRequestRepository.findById(orderRequestId)).thenReturn(Optional.of(mockOrderRequest));

        // Simular a chamada do serviço applyDiscount
        Mockito.when(orderRequestController.applyDiscount(Mockito.any(UUID.class), Mockito.any()))
                .thenAnswer(invocation -> {
                    UUID orderId = invocation.getArgument(0);
                    Map<String, Double> requestBody = invocation.getArgument(1);

                    // Extrair o percentual de desconto do corpo da solicitação
                    Double discountPercentageFromRequest = requestBody.get("discountPercentage");

                    // Mock do ProductService
                    ProductService mockProductServicE = new ProductService();
                    mockProductServicE.setId(productServiceId);
                    mockProductServicE.setName("Produto 1");
                    mockProductServicE.setPrice(BigDecimal.valueOf(50.0));
                    mockProductServicE.setService(false);
                    mockProductServicE.setActive(true);

                    // Mock do OrderRequestItem com o ProductService mockado
                    OrderRequestItem orderRequestIteM = new OrderRequestItem();
                    orderRequestIteM.setProductService(mockProductServicE);
                    orderRequestIteM.setQuantity(2);

                    // Criar um OrderRequest mockado com o desconto aplicado
                    OrderRequest updatedOrderRequest = new OrderRequest();
                    updatedOrderRequest.setId(orderId);
                    updatedOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0 - (100.0 * discountPercentageFromRequest / 100.0)));
                    updatedOrderRequest.setOpen(true);
                    updatedOrderRequest.setItems(Collections.singletonList(orderRequestIteM));

                    return ResponseEntity.ok(updatedOrderRequest);

        });

        // Realizar a chamada ao endpoint
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/orderRequest/{orderRequestId}/apply-discount", orderRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonMap("discountPercentage", discountPercentage))))
                .andExpect(status().isOk())
                .andReturn();

        // Converter a resposta em um objeto OrderRequest
        OrderRequest updatedOrderRequest = objectMapper.readValue(result.getResponse().getContentAsString(), OrderRequest.class);

        // Verificar se os detalhes do pedido foram atualizados corretamente
        assertEquals(mockOrderRequest.getId(), updatedOrderRequest.getId());
        assertEquals(mockOrderRequest.getIsOpen(), updatedOrderRequest.getIsOpen());

        // Verificar se os detalhes do ProductService foram mantidos no OrderRequestItem
        OrderRequestItem updatedOrderRequestItem = updatedOrderRequest.getItems().get(0);
        assertEquals(mockProductService.getId(), updatedOrderRequestItem.getProductService().getId());
        assertEquals(mockProductService.getName(), updatedOrderRequestItem.getProductService().getName());
        assertEquals(mockProductService.getPrice(), updatedOrderRequestItem.getProductService().getPrice());

        // Exibir os detalhes do pedido no console
        System.out.println("Detalhes do Pedido Após Aplicar o Desconto:");
        System.out.println("ID do Pedido: " + updatedOrderRequest.getId());
        System.out.println("Total Amount: " + updatedOrderRequest.getTotalAmount());
        System.out.println("Está Aberto: " + updatedOrderRequest.getIsOpen());
        System.out.println("Detalhes do Item do Pedido:");
        System.out.println("ID do ProductService: " + updatedOrderRequestItem.getProductService().getId());
        System.out.println("Nome do Produto: " + updatedOrderRequestItem.getProductService().getName());
        System.out.println("Preço do Produto: " + updatedOrderRequestItem.getProductService().getPrice());
    }


    @Test
    public void testGetAllWithFilter() throws Exception {
        // Simulando IDs válidos
        UUID orderRequestId = UUID.randomUUID();
        UUID productServiceId = UUID.randomUUID();
        UUID orderRequestItemId = UUID.randomUUID(); // Novo UUID para o item

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem = new OrderRequestItem();
        orderRequestItem.setId(orderRequestItemId); // Definindo o ID do item
        orderRequestItem.setProductService(mockProductService);
        orderRequestItem.setQuantity(2);

        // Mock do OrderRequest com o OrderRequestItem
        OrderRequest mockOrderRequest = new OrderRequest();
        mockOrderRequest.setId(orderRequestId);
        mockOrderRequest.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest.setOpen(true);
        mockOrderRequest.setItems(Collections.singletonList(orderRequestItem));


        // Simulando a lista de OrderRequest retornada pelo serviço com filtro
        List<OrderRequest> mockOrderRequestListWithFilter = new ArrayList<>();
        mockOrderRequestListWithFilter.add(mockOrderRequest); // Adiciona o produto mockado à lista

        // Configurando o comportamento do serviço mock com filtro
        Mockito.when(orderRequestController.getAllWithFilter(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String filter = invocation.getArgument(0);
                    if (filter.equals("produto")) { // Verifica se o filtro é "produto"
                        return ResponseEntity.ok(mockOrderRequestListWithFilter); // Retorna a lista encapsulada em um ResponseEntity
                    } else {
                        return ResponseEntity.notFound().build(); // Retorna uma resposta 404 para qualquer outro filtro
                    }
                });


        // Chamada ao endpoint para obter os dados filtrados
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/orderRequest/filter")
                        .param("filtro", "produto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Extrair e imprimir os dados retornados
        String content = result.getResponse().getContentAsString();
        System.out.println("Dados retornados:");
        System.out.println(content);
    }





    @Test
    public void testGetAllWithFilterPaged() throws Exception {
        // Simulando IDs válidos
        UUID orderRequestId1 = UUID.randomUUID();
        UUID orderRequestId2 = UUID.randomUUID();
        UUID productServiceId = UUID.randomUUID();
        UUID orderRequestItemId1 = UUID.randomUUID();
        UUID orderRequestItemId2 = UUID.randomUUID();

        // Mock do ProductService
        ProductService mockProductService = new ProductService();
        mockProductService.setId(productServiceId);
        mockProductService.setName("Produto 1");
        mockProductService.setPrice(BigDecimal.valueOf(50.0));
        mockProductService.setService(false);
        mockProductService.setActive(true);

        // Mock do OrderRequestItem com o ProductService mockado
        OrderRequestItem orderRequestItem1 = new OrderRequestItem();
        orderRequestItem1.setId(orderRequestItemId1); // Definindo o ID do item 1
        orderRequestItem1.setProductService(mockProductService);
        orderRequestItem1.setQuantity(2);

        OrderRequestItem orderRequestItem2 = new OrderRequestItem();
        orderRequestItem2.setId(orderRequestItemId2); // Definindo o ID do item 2
        orderRequestItem2.setProductService(mockProductService);
        orderRequestItem2.setQuantity(3);

        // Mock do OrderRequest com os OrderRequestItems
        OrderRequest mockOrderRequest1 = new OrderRequest();
        mockOrderRequest1.setId(orderRequestId1);
        mockOrderRequest1.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrderRequest1.setOpen(true);
        mockOrderRequest1.setItems(Collections.singletonList(orderRequestItem1));

        OrderRequest mockOrderRequest2 = new OrderRequest();
        mockOrderRequest2.setId(orderRequestId2);
        mockOrderRequest2.setTotalAmount(BigDecimal.valueOf(150.0));
        mockOrderRequest2.setOpen(true);
        mockOrderRequest2.setItems(Collections.singletonList(orderRequestItem2));

        List<OrderRequest> orderRequests = Arrays.asList(mockOrderRequest1, mockOrderRequest2);
        PageImpl<OrderRequest> page = new PageImpl<>(orderRequests, PageRequest.of(0, 10), orderRequests.size());

        // Simulando a resposta do serviço
        Page<OrderRequest> orderRequestPage = new PageImpl<>(orderRequests);

        // Construindo um ResponseEntity que encapsula a página simulada
        ResponseEntity<Page<OrderRequest>> responseEntity = ResponseEntity.ok(orderRequestPage);

        Mockito.when(orderRequestController.getAllWithFilterPaged(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(responseEntity);

        // Chamada ao endpoint para obter os dados filtrados paginados
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/orderRequest/filter-page")
                        .param("filtro", "produto")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Extrair e imprimir os dados retornados
        String content = result.getResponse().getContentAsString();
        System.out.println("Dados retornados:");
        System.out.println(content);

        // Converte a resposta JSON em um objeto Page<OrderRequest>
        Map<String, Object> responseMap = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        List<OrderRequest> responseOrderRequestList = objectMapper.convertValue(responseMap.get("content"), new TypeReference<List<OrderRequest>>() {});
        long totalElements = (int) responseMap.get("totalElements");
        int totalPages = (int) responseMap.get("totalPages");
        boolean last = (boolean) responseMap.get("last");
        boolean first = (boolean) responseMap.get("first");
        int number = (int) responseMap.get("number");
        int size = (int) responseMap.get("size");
        int numberOfElements = (int) responseMap.get("numberOfElements");

        // Criando um objeto Page<OrderRequest> usando os valores extraídos
        PageImpl<OrderRequest> returnedPage = new PageImpl<>(responseOrderRequestList, PageRequest.of(number, size), totalElements);

        // Verificar se o número de itens na página retornada é o mesmo que o número de itens na página simulada
        assertEquals(page.getContent().size(), returnedPage.getContent().size());

        // Verificar se o número total de elementos é o mesmo
        assertEquals(page.getTotalElements(), returnedPage.getTotalElements());

        // Verificar se o número total de páginas é o mesmo
        assertEquals(page.getTotalPages(), returnedPage.getTotalPages());

        // Verificar se o número da página é o mesmo
        assertEquals(page.getNumber(), returnedPage.getNumber());

        // Verificar se o número de elementos na página atual é o mesmo
        assertEquals(page.getNumberOfElements(), returnedPage.getNumberOfElements());


    }






}
