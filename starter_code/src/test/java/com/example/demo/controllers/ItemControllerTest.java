package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void testFindAllItems_OK() {
        when(itemRepo.findAll()).thenReturn(createListOfTestItem(5));
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        List<Item> items = response.getBody();
        assertNotNull(items);

        assertEquals(5, response.getBody().size());
    }

    @Test
    public void testFindItemsByName_OK() {
        Item findedItem = createListOfTestItem(5).get(3);
        when(itemRepo.findByName("test item 3")).thenReturn(Collections.singletonList(findedItem));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("test item 3");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        List<Item> items = response.getBody();
        assertNotNull(items);

        assertEquals(items.get(0).getName(), "test item 3");
    }

    private Item createTestItem() {
        Item i = new Item();
        i.setId(0L);
        i.setDescription("test item des");
        i.setName("test item name");
        i.setPrice(BigDecimal.valueOf(12L));
        return i;
    }

    private List<Item> createListOfTestItem(int quantity) {
        List<Item> items = new ArrayList<>();
        IntStream.range(0, quantity)
                .forEach(i -> {
                    Item item = new Item();
                    item.setId((long) 0);
                    item.setName("test item " + i);
                    item.setDescription("test item des " + i);
                    item.setPrice(BigDecimal.valueOf(i));
                    items.add(item);
                });

        return items;
    }
}
