package Shoppingcart.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    private ShoppingCart emptyCart;

    @BeforeEach
    void setUp() {
        emptyCart = new ShoppingCart("cart-1", Collections.emptyList(), false);
    }

    @Test
    void ShouldAddNewItemToCart() {
        ShoppingCart.LineItem newItem = new ShoppingCart.LineItem("prod-1", "16GB RAM", 1);
        ShoppingCart updatedCart = emptyCart.addItem(newItem);
        
        assertEquals(1, updatedCart.items().size());
        assertEquals(newItem.productId(), updatedCart.findItemByProductId("prod-1").get().productId());

    }

    @Test
    void shouldUpdateQuantityOfExistingItem() {
        ShoppingCart.LineItem item = new ShoppingCart.LineItem("prod-1", "16GB RAM", 1);
        ShoppingCart cartWithItem = emptyCart.addItem(item);
        
        ShoppingCart.LineItem additionalItem = new ShoppingCart.LineItem("prod-1", "16GB RAM", 2);
        ShoppingCart updatedCart = cartWithItem.addItem(additionalItem);
        
        assertEquals(1, updatedCart.items().size());
        assertEquals(3, updatedCart.findItemByProductId("prod-1").get().quantity());
    }

    @Test
    void shouldRemoveAnItemFromCart() {
        // Given a cart with two items
        ShoppingCart cartWithTwoItems = emptyCart.addItem(new ShoppingCart.LineItem("product-1", "16GB RAM", 1));
        cartWithTwoItems = cartWithTwoItems.addItem(new ShoppingCart.LineItem("product-2", "250GB HardDrive", 1));

        // When we remove one of the items
        ShoppingCart updatedCart = cartWithTwoItems.removeItem("product-1");

        // Then the cart should only have one item left
        assertEquals(1, updatedCart.items().size());
        assertFalse(updatedCart.findItemByProductId("product-1").isPresent());
        assertTrue(updatedCart.findItemByProductId("product-2").isPresent());
    }

    @Test
    void shouldBeUnchangedWhenRemovingNonExistentItem() {
        // Given a cart with a single item
        ShoppingCart cartWithOneItem = emptyCart.addItem(new ShoppingCart.LineItem("product-1", "16GB RAM", 1));
        
        // When we try to remove an item that is not in the cart
        ShoppingCart updatedCart = cartWithOneItem.removeItem("product-999");

        // Then the cart should remain unchanged
        assertEquals(1, updatedCart.items().size());
        assertEquals(cartWithOneItem.items().get(0).productId(), updatedCart.items().get(0).productId());
    }

    @Test
    void shouldSetCheckedOutFlagToTrue() {
        // Given an active cart
        ShoppingCart activeCart = emptyCart.addItem(new ShoppingCart.LineItem("product-1", "16GB RAM", 1));
        assertFalse(activeCart.checkedOut());

        // When we call checkOut
        ShoppingCart checkedOutCart = activeCart.checkOut();

        // Then the checkedOut flag should be true
        assertTrue(checkedOutCart.checkedOut());
    }



}
