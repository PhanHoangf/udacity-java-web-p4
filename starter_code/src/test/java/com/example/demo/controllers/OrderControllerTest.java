package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void test_submit_OK() {
        String testUsername = "test username";
        User testUser = createTestUser();
        testUser.getCart().setItems(createListOfTestItem(5));

        when(userRepo.findByUsername(testUsername)).thenReturn(testUser);

        UserOrder userOrder = new UserOrder();
        userOrder.setItems(UserOrder.createFromCart(testUser.getCart()).getItems());

        UserOrder setupUserOrder = UserOrder.createFromCart(testUser.getCart());
        userOrder.setItems(setupUserOrder.getItems());

        ResponseEntity<UserOrder> response = orderController.submit(testUsername);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(0, order.getId());
        assertEquals(order.getItems().size(), 5);
    }

    @Test
    public void test_getOrdersForUser_OK() {
        String testUsername = "test username";
        User testUser = createTestUser();
        testUser.getCart().setItems(createListOfTestItem(5));

        when(userRepo.findByUsername(testUsername)).thenReturn(testUser);
        when(orderRepo.findByUser(testUser)).thenReturn(createListUserOrder(testUser));

        UserOrder userOrder = new UserOrder();
        userOrder.setItems(UserOrder.createFromCart(testUser.getCart()).getItems());

        UserOrder setupUserOrder = UserOrder.createFromCart(testUser.getCart());
        userOrder.setItems(setupUserOrder.getItems());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(testUsername);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        for (UserOrder order : orders) {
            assertEquals(order.getUser().getUsername(), testUsername);
        }
    }

    private User createTestUser() {
        User u = new User();
        u.setId(0);
        u.setUsername("test username");
        u.setCart(new Cart());
        return u;
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

    private List<UserOrder> createListUserOrder(User user) {
        List<UserOrder> orders = new ArrayList<>();
        IntStream.range(0, 5)
                .forEach(i -> {
                    UserOrder order = new UserOrder();
                    order.setId((long) i);
                    order.setUser(user);
                    orders.add(order);
                });

        return orders;
    }
}
