package Shoppingcart.domain;

import Shoppingcart.domain.ShoppingCart.LineItem;
import akka.javasdk.annotations.TypeName;

public sealed interface ShoppingCartEvent {

    String cartId();

    @TypeName("item-added")
    record ItemAdded(String cartId, LineItem item) implements ShoppingCartEvent {}

    @TypeName("item-removed")
    record ItemRemoved(String cartId, String productId) implements ShoppingCartEvent {}

    @TypeName("cart-checked-out")
    record CartCheckedOut(String cartId) implements ShoppingCartEvent {}

}
