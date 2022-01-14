package com.example.eComPractice.controllers;

import com.example.eComPractice.TestUtils;
import com.example.eComPractice.model.persistence.Cart;
import com.example.eComPractice.model.persistence.Item;
import com.example.eComPractice.model.persistence.User;
import com.example.eComPractice.model.persistence.UserOrder;
import com.example.eComPractice.model.persistence.repositories.OrderRepository;
import com.example.eComPractice.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrderHappyPath() {
        User user = createUser();
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(user.getCart().getItems(), order.getItems());
        assertEquals(user.getCart().getTotal(), order.getTotal());
        assertEquals(user.getUsername(), order.getUser().getUsername());
    }

    @Test
    public void submitOrderFail() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("nobody");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getUserOrderHappyPath() {
        List<UserOrder> orders = createUserOrder();
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(orderRepository.findByUser(any(User.class))).thenReturn(orders);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> list = response.getBody();
        assertEquals(orders.size(), list.size());
        assertEquals(orders.get(0).getId(), list.get(0).getId());
        assertEquals(orders.get(0).getUser().getId(), list.get(0).getUser().getId());
    }

    @Test
    public void getUserOrderFail() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("nobody");
        assertEquals(404, response.getStatusCodeValue());
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1l);
        item.setPrice(new BigDecimal(200));
        item.setName("test");
        item.setDescription("test description");
        return item;
    }

    private User createUser() {
        Item item = createItem();
        Cart cart = new Cart();
        User user = new User();
        user.setId(1l);
        user.setUsername("testUser");
        user.setCart(cart);
        cart.setUser(user);
        cart.addItem(item);
        return user;
    }

    private List<UserOrder> createUserOrder () {
        Item item = createItem();
        User user = createUser();
        UserOrder order = new UserOrder();
        order.setId(2l);
        order.setItems(Collections.singletonList(item));
        order.setUser(user);
        return Collections.singletonList(order);
    }
}
