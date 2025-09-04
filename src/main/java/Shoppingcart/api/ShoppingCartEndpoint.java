package Shoppingcart.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Shoppingcart.application.ShoppingCartEntity;
import Shoppingcart.domain.ShoppingCart;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;


@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("shoppingcarts")
public class ShoppingCartEndpoint extends AbstractHttpEndpoint {

    private final ComponentClient componentClient;
    private final static Logger logger = LoggerFactory.getLogger(ShoppingCartEndpoint.class.getName());

    public ShoppingCartEndpoint(ComponentClient componentClient) {
        this.componentClient = componentClient;
    }

    @Put("/{cartId}/item")
    public HttpResponse addItem(String cartId, ShoppingCart.LineItem item) {
        logger.info("Received request to add item to cart: {}, item: {}", cartId, item);
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::addItem)
            .invoke(item);
        return HttpResponses.ok();
    }

    @Delete("/{cartId}/item/{productId}")
    public HttpResponse removeItem(String cartId, String productId) {
        logger.info("Received request to remove item: {} from cart: {}", productId, cartId);
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::removeItem)
            .invoke(productId);
        return HttpResponses.ok();
    }

    @Post("/{cartId}/checkout")
    public HttpResponse checkout(String cartId) {
        logger.info("Received request to checkout cart: {}", cartId);
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::checkout)
            .invoke();
        return HttpResponses.ok();
    }

    @Get("/{cartId}")
    public HttpResponse getCart(String cartId) {
        logger.info("Received request to get cart: {}", cartId);
        ShoppingCart cart = componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::getCart)
            .invoke();            
        return HttpResponses.ok(cart);
    }

    
    

}
