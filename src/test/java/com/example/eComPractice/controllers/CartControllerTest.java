package com.example.eComPractice.controllers;

import com.example.eComPractice.TestUtils;
import com.example.eComPractice.model.persistence.Cart;
import com.example.eComPractice.model.persistence.Item;
import com.example.eComPractice.model.persistence.User;
import com.example.eComPractice.model.persistence.repositories.CartRepository;
import com.example.eComPractice.model.persistence.repositories.ItemRepository;
import com.example.eComPractice.model.persistence.repositories.UserRepository;
import com.example.eComPractice.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addItemToCartHappyPath() {
        Cart cart = new Cart();
        User user = new User();
        user.setUsername("test");
        user.setCart(cart);
        Item item = new Item();
        item.setName("testItem");
        item.setPrice(new BigDecimal(100));
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(
                new ModifyCartRequest("test", 1, 2)
        );

        assertEquals(200, response.getStatusCodeValue());

        Cart foundCart = response.getBody();
        assertNotNull(foundCart);
        assertEquals(new BigDecimal(100 * 2), foundCart.getTotal());
        assertEquals(Arrays.asList(item, item), cart.getItems());
    }

    @Test
    public void removeItemFromCartHappyPath() {
        Cart cart = new Cart();
        User user = new User();
        user.setUsername("test");
        user.setCart(cart);
        Item item = new Item();
        item.setName("testItem");
        item.setPrice(new BigDecimal(100));

        cart.addItem(item);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(
                new ModifyCartRequest("test", 1, 1)
        );

        assertEquals(200, response.getStatusCodeValue());
        Cart foundCart = response.getBody();
        assertNotNull(foundCart);
        // https://stackoverflow.com/questions/20645922/how-do-force-bigdecimal-from-rounding-in-junit-assertequals
        BigDecimal expectedCartValue = (new BigDecimal("0"));
        BigDecimal actualCartValue = foundCart.getTotal();
        Double epsilon = Double.valueOf(expectedCartValue.subtract(actualCartValue).toString());
        assertTrue(epsilon < 0.001);
        Assert.assertEquals(0, foundCart.getItems().size());
    }
}
