package Shoppingcart.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import Shoppingcart.domain.ShoppingCart;
import Shoppingcart.domain.ShoppingCartEvent.ItemAdded;
import akka.javasdk.testkit.EventSourcedTestKit;
import akka.Done;

public class ShoppingCartEntityTest {

    private final ShoppingCart.LineItem hynixRAM = 
        new ShoppingCart.LineItem("16GB-RAM", "Hynix RAM - 16 GB", 1);

    @Test
    public void testAddLineItem() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new); // <1>

        {
            var result = testKit.method(ShoppingCartEntity::addItem).invoke(hynixRAM); // <2>
            assertEquals(Done.getInstance(), result.getReply()); // <3>

            var itemAdded = result.getNextEventOfType(ItemAdded.class);
            assertEquals(10, itemAdded.item().quantity()); // <4>
        }

        // actually we want more akka tshirts
        {
            var result = testKit
                .method(ShoppingCartEntity::addItem)
                .invoke(hynixRAM.withQuantity(5)); // <5>
            assertEquals(Done.getInstance(), result.getReply());

            var itemAdded = result.getNextEventOfType(ItemAdded.class);
            assertEquals(5, itemAdded.item().quantity());
        }

        {
            assertEquals(testKit.getAllEvents().size(), 2); // <6>
            var result = testKit.method(ShoppingCartEntity::getCart).invoke(); // <7>
            assertEquals(
                new ShoppingCart("testkit-entity-id", List.of(hynixRAM.withQuantity(15)), false),
                result.getReply()
            );
        }
    }
    
}
