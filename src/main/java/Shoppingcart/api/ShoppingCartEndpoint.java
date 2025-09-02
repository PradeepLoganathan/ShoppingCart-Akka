package Shoppingcart.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Shoppingcart.application.ShoppingCartEntity;
import Shoppingcart.domain.ShoppingCart;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.http.HttpResponses;


@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("shoppingcarts")
public class ShoppingCartEndpoint {

    private final ComponentClient componentClient;
    private final static Logger logger = LoggerFactory.getLogger(ShoppingCartEndpoint.class.getName());

    public ShoppingCartEndpoint(ComponentClient componentClient) {
        this.componentClient = componentClient;
    }

    public HttpResponse addItem(String cartId, ShoppingCart.LineItem item) {
        logger.info("Received request to add item to cart: {}, item: {}", cartId, item);
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::addItem)
            .invoke(item);
        return HttpResponses.ok();
    }
    

}
