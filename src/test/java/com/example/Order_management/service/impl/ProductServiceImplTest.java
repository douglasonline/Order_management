package com.example.Order_management.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.ProductServiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import java.util.UUID;




@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceService productServiceService;


    @MockBean
    private ProductServiceRepository productServiceRepository;


    // Instância do ObjectMapper para serialização/deserialização de objetos JSON
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }



    @Test
    void testCreate() {
        // Mock do objeto ProductService a ser criado
        ProductService productServiceToCreate = new ProductService();
        productServiceToCreate.setName("Produto Teste");
        productServiceToCreate.setPrice(new BigDecimal("10.00"));
        productServiceToCreate.setService(false);
        productServiceToCreate.setActive(true);

        // Mock do objeto ProductService criado pelo serviço
        ProductService createdProductService = new ProductService();
        createdProductService.setId(UUID.randomUUID()); // Definindo manualmente um ID para simular a geração automática
        createdProductService.setName("Produto Teste");
        createdProductService.setPrice(new BigDecimal("10.00"));
        createdProductService.setService(false);
        createdProductService.setActive(true);

        // Mock do serviço para retornar o objeto ProductService criado
        Mockito.when(productServiceService.create(Mockito.any(ProductService.class)))
                .thenReturn(createdProductService);

        // Chama o método create do ProductServiceServiceImpl
        ProductService returnedProductService = productServiceService.create(productServiceToCreate);

        // Verifica se o ProductService retornado é igual ao esperado
        assertEquals(createdProductService, returnedProductService);

        System.out.println();
        // Exibe os dados retornados no console
        System.out.println("Produto criado:");
        System.out.println("ID: " + createdProductService.getId());
        System.out.println("Nome: " + createdProductService.getName());
        System.out.println("Preço: " + createdProductService.getPrice());
        System.out.println("Serviço: " + createdProductService.isService());
        System.out.println("Ativo: " + createdProductService.isActive());
    }



    @Test
    public void testGetAllWithFilter() {
        // Mock do filtro
        String filtro = "produto";

        // Simulando a lista de produtos retornada pelo serviço
        List<ProductService> mockProductServiceList = new ArrayList<>();

        // Criando produtos de exemplo
        ProductService productService1 = new ProductService();
        productService1.setId(UUID.randomUUID());
        productService1.setName("Produto 1");
        productService1.setPrice(BigDecimal.valueOf(20.0));
        productService1.setActive(true);
        productService1.setService(false);
        mockProductServiceList.add(productService1);

        ProductService productService2 = new ProductService();
        productService2.setId(UUID.randomUUID());
        productService2.setName("Produto 2");
        productService2.setPrice(BigDecimal.valueOf(30.0));
        productService2.setActive(true);
        productService2.setService(false);
        mockProductServiceList.add(productService2);

        // Configurando o comportamento do serviço mock
        Mockito.when(productServiceService.getAllWithFilter(Mockito.anyString())).thenReturn(mockProductServiceList);

        // Chamando a função que queremos testar
        List<ProductService> resultList = productServiceService.getAllWithFilter(filtro);

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(productServiceService).getAllWithFilter(Mockito.anyString());

        // Verificando se a lista retornada não é nula e contém os elementos esperados
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size());

        System.out.println();
        // Imprimindo os detalhes dos produtos no console
        System.out.println("Produtos encontrados com filtro '" + filtro + "':");
        for (ProductService product : resultList) {
            System.out.println("ID: " + product.getId());
            System.out.println("Nome: " + product.getName());
            System.out.println("Preço: " + product.getPrice());
            System.out.println("Ativo: " + product.isActive());
            System.out.println("Serviço: " + product.isService());
            System.out.println();
        }
    }

    @Test
    public void testGetAllWithPaginationWithFilter() throws JsonProcessingException {
        // Mock do filtro
        String filtro = "produto";

        // Simulando a lista de produtos retornada pelo serviço
        List<ProductService> mockProductServiceList = new ArrayList<>();

        // Criando produtos de exemplo
        ProductService productService1 = new ProductService();
        productService1.setId(UUID.randomUUID());
        productService1.setName("Produto 1");
        productService1.setPrice(BigDecimal.valueOf(20.0));
        productService1.setActive(true);
        productService1.setService(false);
        mockProductServiceList.add(productService1);

        ProductService productService2 = new ProductService();
        productService2.setId(UUID.randomUUID());
        productService2.setName("Produto 2");
        productService2.setPrice(BigDecimal.valueOf(30.0));
        productService2.setActive(true);
        productService2.setService(false);
        mockProductServiceList.add(productService2);

        // Criando uma página de exemplo
        Page<ProductService> mockPage = new PageImpl<>(mockProductServiceList, PageRequest.of(0, 10), mockProductServiceList.size());

        // Configurando o comportamento do serviço mock
        Mockito.when(productServiceService.getAllWithFilter(Mockito.anyString(), Mockito.any()))
                .thenReturn(mockPage);

        // Chamando a função que queremos testar
        Page<ProductService> resultPage = productServiceService.getAllWithFilter(filtro, PageRequest.of(0, 10));

        // Verificando se o serviço foi chamado corretamente com o filtro
        Mockito.verify(productServiceService).getAllWithFilter(Mockito.anyString(), Mockito.any());

        // Imprimindo os detalhes dos produtos na página no console
        System.out.println("Produtos encontrados com filtro '" + filtro + "':");
        resultPage.forEach(product -> {
            System.out.println("ID: " + product.getId());
            System.out.println("Nome: " + product.getName());
            System.out.println("Preço: " + product.getPrice());
            System.out.println("Ativo: " + product.isActive());
            System.out.println("Serviço: " + product.isService());
            System.out.println();
        });

        // Serializando a página para JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String pageJson = objectMapper.writeValueAsString(mockPage);
        System.out.println("JSON da página:");
        System.out.println(pageJson);
    }


}
