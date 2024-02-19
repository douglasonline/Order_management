package com.example.Order_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.controller.ProductServiceController;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.service.ProductServiceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceController productServiceController;

    // Instância do ObjectMapper para serialização/deserialização de objetos JSON
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }



    @Test
    public void testCreateProductService() throws Exception {
        // JSON de exemplo para criar um novo serviço de produto
        String jsonRequest = "{\"name\":\"Produto Teste\",\"price\":10.00,\"isService\":false,\"isActive\":true}";

        // Converte o JSON de requisição em um objeto ProductService
        ObjectMapper objectMapper = new ObjectMapper();
        ProductService productService = objectMapper.readValue(jsonRequest, ProductService.class);

        // Simulando o serviço para retornar o objeto ProductService criado a partir do JSON de requisição
        productService.setId(UUID.randomUUID()); // Definindo manualmente um ID para simular a geração automática

        // Criando um ResponseEntity com o produto criado e o status OK
        ResponseEntity<ProductService> responseEntity = ResponseEntity.ok(productService);

        // Configurando o comportamento do mock do controlador para retornar o ResponseEntity criado
        Mockito.when(productServiceController.create(Mockito.any(ProductService.class))).thenReturn(responseEntity);


        // Realiza a requisição POST para criar o serviço de produto
        MvcResult result = mockMvc.perform(post("/api/productService")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Obtém o conteúdo da resposta
        String responseContent = result.getResponse().getContentAsString();

        // Verifica se a resposta não está vazia
        assertTrue(responseContent != null && !responseContent.isEmpty(), "Response content is empty");

        // Converte a resposta JSON em um objeto ProductService apenas se a resposta não estiver vazia
        ProductService createdProductService = null;
        if (!responseContent.isEmpty()) {
            createdProductService = objectMapper.readValue(responseContent, ProductService.class);

            // Verifica se os detalhes do produto foram incluídos corretamente na resposta
            assertEquals("Produto Teste", createdProductService.getName());
            assertEquals(new BigDecimal("10.00"), createdProductService.getPrice());
            assertNotNull(createdProductService.getId()); // Verifica se o ID foi gerado corretamente
        }

        // Imprime o conteúdo da resposta
        System.out.println("Response Content: " + responseContent);

    }


    @Test
    public void testGetAllWithFilter() throws Exception {
        // JSON de exemplo para criar um novo serviço de produto
        String jsonRequest = "{\"name\":\"Produto Teste\",\"price\":10.00,\"isService\":false,\"isActive\":true}";

        // Converte o JSON de requisição em um objeto ProductService
        ObjectMapper objectMapper = new ObjectMapper();
        ProductService productService = objectMapper.readValue(jsonRequest, ProductService.class);
        productService.setId(UUID.randomUUID()); // Definindo manualmente um ID para simular a geração automática

        // Simulando um mock da classe ProductService
        ProductService mockProductService = Mockito.mock(ProductService.class);
        Mockito.when(mockProductService.getId()).thenReturn(productService.getId());
        Mockito.when(mockProductService.getName()).thenReturn(productService.getName());
        Mockito.when(mockProductService.getPrice()).thenReturn(productService.getPrice());
        Mockito.when(mockProductService.isService()).thenReturn(productService.isService());
        Mockito.when(mockProductService.isActive()).thenReturn(productService.isActive());

        // Simulando a lista de ProductService retornada pelo serviço
        List<ProductService> mockProductServiceList = new ArrayList<>();
        mockProductServiceList.add(mockProductService);

        // Simulando a lista de ProductService retornada pelo serviço com filtro
        List<ProductService> mockProductServiceListWithFilter = new ArrayList<>();
        mockProductServiceListWithFilter.add(mockProductService); // Adiciona o produto mockado à lista

        // Configurando o comportamento do serviço mock com filtro
        Mockito.when(productServiceController.getAllWithFilter(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String filter = invocation.getArgument(0);
                    if (filter.equals("produto")) { // Verifica se o filtro é "produto"
                        return ResponseEntity.ok(mockProductServiceListWithFilter); // Retorna a lista encapsulada em um ResponseEntity
                    } else {
                        return ResponseEntity.notFound().build(); // Retorna uma resposta 404 para qualquer outro filtro
                    }
                });

        // Realizando a requisição GET para buscar todos os ProductService com base no filtro
        MvcResult result = mockMvc.perform(get("/api/productService/allWithFilter")
                        .param("filtro", "produto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Obtendo o conteúdo da resposta
        String responseContent = result.getResponse().getContentAsString();

        // Converte a resposta JSON em uma lista de objetos ProductService
        List<ProductService> productList = objectMapper.readValue(responseContent, new TypeReference<List<ProductService>>() {});

        // Verifica se a lista não está vazia antes de prosseguir
        if (!productList.isEmpty()) {
            // Pega o primeiro produto da lista
            ProductService createdProductService = productList.get(0);

            // Verifica se os detalhes do produto foram incluídos corretamente na resposta
            assertEquals(productService.getName(), createdProductService.getName());
            assertEquals(productService.getPrice(), createdProductService.getPrice());
            assertEquals(productService.isService(), createdProductService.isService());
            assertEquals(productService.isActive(), createdProductService.isActive());

            // Imprime o conteúdo da resposta
            System.out.println("Response Content: " + responseContent);
        } else {
            System.out.println("A lista de produtos está vazia.");
        }
    }


    @Test
    public void testGetAllWithPaginationWithFilter() throws Exception {
        // Mock do serviço
        ProductService product1 = new ProductService();
        product1.setId(UUID.randomUUID());
        product1.setName("Produto 1");
        product1.setPrice(BigDecimal.valueOf(20.0));

        ProductService product2 = new ProductService();
        product2.setId(UUID.randomUUID());
        product2.setName("Produto 2");
        product2.setPrice(BigDecimal.valueOf(30.0));

        // Simulando a lista de produtos
        List<ProductService> productList = Arrays.asList(product1, product2);

        // Simulando a resposta do serviço
        Page<ProductService> productPage = new PageImpl<>(productList);

        // Construindo um ResponseEntity que encapsula a página simulada
        ResponseEntity<Page<ProductService>> responseEntity = ResponseEntity.ok(productPage);

        Mockito.when(productServiceController.getAllWithPaginationWithFilter(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(responseEntity);

        // Chamada ao endpoint
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/productService/allWithPagination?filtro=produto&page=0&size=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Obtendo o conteúdo da resposta
        String responseContent = result.getResponse().getContentAsString();

        // Converte a resposta JSON em um objeto JsonNode
        JsonNode jsonNode = objectMapper.readTree(responseContent);

        // Obtém os elementos da lista de produtos
        JsonNode contentNode = jsonNode.get("content");

        // Converte a lista de produtos em uma lista de ProductService
        List<ProductService> responseProductList = objectMapper.convertValue(contentNode, new TypeReference<List<ProductService>>() {});

        // Obtém os outros campos necessários para construir um Page<ProductService>
        int totalElements = jsonNode.get("totalElements").asInt();
        int totalPages = jsonNode.get("totalPages").asInt();
        boolean last = jsonNode.get("last").asBoolean();
        int size = jsonNode.get("size").asInt();
        int number = jsonNode.get("number").asInt();
        int numberOfElements = jsonNode.get("numberOfElements").asInt();
        boolean first = jsonNode.get("first").asBoolean();
        boolean empty = jsonNode.get("empty").asBoolean();

        // Constrói um PageImpl com os dados obtidos
        Page<ProductService> responsePage = new PageImpl<>(responseProductList, PageRequest.of(number, size), totalElements);

        // Exibir os dados para o usuário
        System.out.println("Total de produtos encontrados: " + responsePage.getTotalElements());
        System.out.println("Número da página: " + responsePage.getNumber());
        System.out.println("Número de produtos nesta página: " + responsePage.getNumberOfElements());
        System.out.println("Total de páginas: " + responsePage.getTotalPages());
        System.out.println("Última página: " + responsePage.isLast());
        System.out.println("Primeira página: " + responsePage.isFirst());
        System.out.println("Tamanho da página: " + responsePage.getSize());

        // Verifica se a lista de produtos na resposta corresponde à lista simulada
        assertEquals(productList.size(), responseProductList.size());
        for (int i = 0; i < productList.size(); i++) {
            ProductService expectedProduct = productList.get(i);
            ProductService actualProduct = responseProductList.get(i);
            assertEquals(expectedProduct.getId(), actualProduct.getId());
            assertEquals(expectedProduct.getName(), actualProduct.getName());
            assertEquals(expectedProduct.getPrice(), actualProduct.getPrice());
            assertEquals(expectedProduct.isService(), actualProduct.isService());
            assertEquals(expectedProduct.isActive(), actualProduct.isActive());
        }
    }



}
