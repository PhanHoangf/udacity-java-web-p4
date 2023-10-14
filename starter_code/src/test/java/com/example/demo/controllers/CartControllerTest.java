package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void testAddTocart_OK() {
        ModifyCartRequest request = new ModifyCartRequest();
        String testUsername = "test username";

        request.setUsername(testUsername);
        request.setItemId(0L);
        request.setQuantity(5);

        User testUser = createTestUser();
        Item item = createTestItem();

        when(userRepo.findByUsername(testUsername)).thenReturn(testUser);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(cart.getItems().size(), 5);
    }

    @Test
    public void testRemoveFromCart_OK() {
        ModifyCartRequest request = new ModifyCartRequest();
        String testUsername = "test username";

        request.setUsername(testUsername);
        request.setItemId(0L);
        request.setQuantity(3);

        User testUser = createTestUser();
        testUser.getCart().setItems(createListOfTestItem(5));

        Item item = createTestItem();

        when(userRepo.findByUsername(testUsername)).thenReturn(testUser);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(cart.getItems().size(), 2);
    }


    @Test
    public void testAddTocart_NoUsername_No_OK() {
        ModifyCartRequest request = new ModifyCartRequest();
        String testUsername = "test username";

        request.setUsername(testUsername);
        request.setItemId(0L);
        request.setQuantity(5);

        User testUser = createTestUser();

        when(userRepo.findByUsername(testUsername)).thenReturn(testUser);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode()
                .value());
    }

    @Test
    public void testAddTocart_NoItem_No_OK() {
        ModifyCartRequest request = new ModifyCartRequest();
        String testUsername = "test username";

        request.setUsername(testUsername);
        request.setItemId(0L);
        request.setQuantity(5);

        when(userRepo.findByUsername(testUsername)).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCode().value());
    }

    private User createTestUser() {
        User u = new User();
        u.setId(0);
        u.setUsername("test username");
        u.setCart(new Cart());
        return u;
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
