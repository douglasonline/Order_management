package com.example.Order_management.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Interface que estende JpaRepository e QuerydslPredicateExecutor
interface CustomRepository<T> extends JpaRepository<T, UUID>, QuerydslPredicateExecutor<T> {
}

class GenericServiceImplTest {

    @Mock
    private CustomRepository<Object> mockRepository;

    @InjectMocks
    private GenericServiceImpl<Object, UUID, CustomRepository<Object>> service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Object> itemList = Arrays.asList(new Object(), new Object());
        when(mockRepository.findAll()).thenReturn(itemList);

        List<Object> result = service.getAll();

        assertEquals(itemList, result);
    }

    @Test
    void testGetAllWithPagination() {
        List<Object> itemList = Arrays.asList(new Object(), new Object());
        Pageable pageable = mock(Pageable.class);
        Page<Object> page = new PageImpl<>(itemList);
        when(mockRepository.findAll(pageable)).thenReturn(page);

        Page<Object> result = service.getAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testGet() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(mockRepository.findById(id)).thenReturn(Optional.of(item));

        Object result = service.get(id);

        assertEquals(item, result);
    }

    @Test
    void testGet_NotFound() {
        UUID id = UUID.randomUUID();
        when(mockRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.get(id));
        assertEquals("Id: " + id, exception.getMessage());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(mockRepository.existsById(id)).thenReturn(true);
        when(mockRepository.save(item)).thenReturn(item);

        Object result = service.update(id, item);

        assertEquals(item, result);
    }

    @Test
    void testUpdate_NotFound() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(mockRepository.existsById(id)).thenReturn(false);

        Object result = service.update(id, item);

        assertNull(result);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        when(mockRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(id));
        verify(mockRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound() {
        UUID id = UUID.randomUUID();
        when(mockRepository.existsById(id)).thenReturn(false);

        EmptyResultDataAccessException exception = assertThrows(EmptyResultDataAccessException.class, () -> service.delete(id));
        assertEquals(1, exception.getExpectedSize());
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Object item = new Object();
        when(mockRepository.findById(id)).thenReturn(Optional.of(item));

        Optional<Object> result = service.findById(id);

        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(mockRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Object> result = service.findById(id);

        assertFalse(result.isPresent());
    }

}



