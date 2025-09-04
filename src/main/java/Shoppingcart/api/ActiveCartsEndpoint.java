package Shoppingcart.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Shoppingcart.projections.ActiveCartEntries;
import Shoppingcart.projections.ActiveCartEntry;
import Shoppingcart.projections.ActiveCartsView;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("activecarts")
public class ActiveCartsEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;
  private static final Logger logger = LoggerFactory.getLogger(ActiveCartsEndpoint.class);

  public ActiveCartsEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get
  public HttpResponse list() {
    logger.info("Listing active carts");
    ActiveCartEntries entries = componentClient
        .forView()
        .method(ActiveCartsView::listActiveCarts)
        .invoke();
    return HttpResponses.ok(entries);
  }

  @Get("/{cartId}")
  public HttpResponse getById(String cartId) {
    logger.info("Fetching active cart: {}", cartId);
    ActiveCartEntries entries = componentClient
        .forView()
        .method(ActiveCartsView::getActiveCart)
        .invoke(cartId);

    if (entries.activecarts() == null || entries.activecarts().isEmpty()) {
      return HttpResponses.notFound();
    } else if (entries.activecarts().size() == 1) {
      ActiveCartEntry entry = entries.activecarts().getFirst();
      return HttpResponses.ok(entry);
    } else {
      // Should not normally happen (cartId is unique), but return the list if multiple exist
      return HttpResponses.ok(entries);
    }
  }
}

