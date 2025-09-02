package Shoppingcart.application;


import org.slf4j.LoggerFactory;

import java.util.Collections;

import org.slf4j.Logger;

import Shoppingcart.domain.ShoppingCart;
import Shoppingcart.domain.ShoppingCartEvent;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import akka.Done;

@ComponentId("shopping-cart")
public class ShoppingCartEntity extends EventSourcedEntity< ShoppingCart, ShoppingCartEvent> {

    private final String entityId;
    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartEntity.class);

    public ShoppingCartEntity(EventSourcedEntityContext context) {
        this.entityId = context.entityId();
    }

    public ShoppingCart emptyState() {
        return new ShoppingCart(entityId,Collections.emptyList(),false);
    } 
    
    public Effect<Done> addItem(ShoppingCart.LineItem item) {
        if (currentState().checkedOut()) {
            logger.warn("Attempted to add item to a checked-out cart: {}", entityId);
            return effects().error("Cannot add items to a checked-out cart.");
        }
        if (item.quantity() <= 0) {
            logger.warn("Attempted to add item with non-positive quantity: {} to cart: {}", item.quantity(), entityId); 
            return effects().error("Quantity must be greater than zero.");
        }
        
        var event = new ShoppingCartEvent.ItemAdded(item);
        logger.info("Emitting ItemAdded event for cart: {}, item: {}", entityId, item);
        return effects()
                .persist(event)
                .thenReply(newState -> Done.getInstance());
    }

    public Effect<Done> removeItem(String productId) {
        if (currentState().checkedOut()) {
            logger.warn("Attempted to remove item from a checked-out cart: {}", entityId);
            return effects().error("Cannot remove items from a checked-out cart.");
        }
        if (currentState().findItemByProductId(productId).isEmpty()) {
            logger.warn("Attempted to remove non-existent item: {} from cart: {}", productId, entityId);
            return effects().error("Item not found in the cart.");
        }
        
        var event = new ShoppingCartEvent.ItemRemoved(productId);
        logger.info("Emitting ItemRemoved event for cart: {}, productId: {}", entityId, productId);
        return effects()
                .persist(event)
                .thenReply(newState -> Done.getInstance());
    }

    public Effect<Done> checkout() {
        if (currentState().checkedOut()) {
            logger.warn("Attempted to checkout an already checked-out cart: {}", entityId);
            return effects().error("Cart is already checked out.");
        }
        if (currentState().items().isEmpty()) {
            logger.warn("Attempted to checkout an empty cart: {}", entityId);
            return effects().error("Cannot checkout an empty cart.");
        }
        
        var event = new ShoppingCartEvent.CartCheckedOut();
        logger.info("Emitting CartCheckedOut event for cart: {}", entityId);
        return effects()
                .persist(event)
                .thenReply(newState -> Done.getInstance());
    }

    public ReadOnlyEffect<ShoppingCart> getCart() {
        return effects().reply(currentState());        
    }

    @Override
    public ShoppingCart applyEvent(ShoppingCartEvent event) {
        
        return switch (event) {
            case ShoppingCartEvent.ItemAdded evt -> currentState().addItem(evt.item());
            case ShoppingCartEvent.ItemRemoved evt -> currentState().removeItem(evt.productId());
            case ShoppingCartEvent.CartCheckedOut evt -> currentState().checkOut();
        };
    }

}
