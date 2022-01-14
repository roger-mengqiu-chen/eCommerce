package com.example.eComPractice.controllers;

import com.example.eComPractice.TestUtils;
import com.example.eComPractice.model.persistence.Item;
import com.example.eComPractice.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        itemController = new ItemController();

        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsByNameHappyPath() {
        Item item = new Item();
        item.setId(1L);
        List<Item> items = Collections.singletonList(item);

        when(itemRepository.findByName(anyString())).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("test");
        assertEquals(200, response.getStatusCodeValue());
        List<Item> list = response.getBody();
        assertNotNull(list);
        assertEquals(items.size(), list.size());
        assertEquals(items.get(0), list.get(0));
    }
}
