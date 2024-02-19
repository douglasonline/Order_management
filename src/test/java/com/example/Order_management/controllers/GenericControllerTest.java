package com.example.Order_management.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.*;

import com.example.Order_management.controller.GenericController;
import com.example.Order_management.controller.OrderRequestController;
import com.example.Order_management.model.OrderRequest;
import com.example.Order_management.model.OrderRequestItem;
import com.example.Order_management.model.ProductService;
import com.example.Order_management.repository.OrderRequestRepository;
import com.example.Order_management.repository.ProductServiceRepository;
import com.example.Order_management.service.GenericService;
import com.example.Order_management.service.OrderRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar o banco de dados real
public class GenericControllerTest {

    @Mock
    private GenericService<Object> service;

    @InjectMocks
    private GenericController<Object> controller;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }



    @Test
    void testGetAllWithoutPagination() {
        List<Object> items = new ArrayList<>();
        // Mock the service to return the list of items
        when(service.getAll()).thenReturn(items);

        // Call the method that prints to console
        ResponseEntity<List<Object>> response = controller.getAllWithoutPagination();

        // Verify that the response status is OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Print the actual console output for debugging
        System.out.println("Actual Console Output: " + outputStreamCaptor.toString().trim());

    }

    @Test
    void testGetAllWithPagination() {
        Page<Object> page = new PageImpl<>(new ArrayList<>());
        Pageable pageable = mock(Pageable.class);
        when(service.getAll(pageable)).thenReturn(page);

        ResponseEntity<Page<Object>> response = controller.getAllWithPagination(pageable);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testGet() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(service.get(id)).thenReturn(item);

        ResponseEntity<?> response = controller.get(id);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(service.update(id, item)).thenReturn(ResponseEntity.ok(item)); // Retorna ResponseEntity<Object>

        ResponseEntity<?> response = controller.update(id, item);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console

    }

    @Test
    void testDeleteSuccess() {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(new Object()));

        ResponseEntity<?> response = controller.delete(id);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console


    }

    @Test
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.delete(id);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console

    }

    @Test
    void testDeleteError() {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(RuntimeException.class);

        ResponseEntity<?> response = controller.delete(id);

        System.out.println("Response Body: " + response.getBody()); // Adiciona esta linha para imprimir a resposta no console

    }

}


