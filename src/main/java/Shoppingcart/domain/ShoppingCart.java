package Shoppingcart.domain;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ShoppingCart(String cartId, List<LineItem> items, boolean checkedOut) {
    public record LineItem(String productId, String name, int quantity) {
        public LineItem withQuantity(int quantity){
            return new LineItem(productId, name, quantity);
        }
    }

    public ShoppingCart addItem(LineItem item) {
        // Find if the item already exists in the cart.
        Optional<LineItem> existingItem = findItemByProductId(item.productId);
        // If it exists, update the quantity; otherwise, add the new item.
        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().quantity + item.quantity;
            List<LineItem> updatedItems = items.stream()
                    .map(li -> li.productId.equals(item.productId) ? li.withQuantity(newQuantity) : li)
                    .collect(Collectors.toList());
            
            return new ShoppingCart(cartId, updatedItems, checkedOut);
        } else {
            List<LineItem> newItems = new java.util.ArrayList<LineItem>(items);
            newItems.add(item);
            return new ShoppingCart(cartId, newItems, checkedOut);
        }
    }

    // Remove an item from the cart by productId.
    public ShoppingCart removeItem(String productId) {
        List<LineItem> updatedItems = items().stream()
                .filter(li -> !li.productId().equals(productId))
                .collect(Collectors.toList());
        
        return new ShoppingCart(cartId, updatedItems, checkedOut);
    }

    public Optional<LineItem> findItemByProductId(String productId) {
        Predicate<LineItem> lineItemExists = LineItem -> LineItem.productId.equals(productId);
        return items.stream().filter(lineItemExists).findFirst();
    }

    // Marks the shopping cart as checked out.
    public ShoppingCart checkOut() {
        return new ShoppingCart(cartId, items, true);
    }

}
