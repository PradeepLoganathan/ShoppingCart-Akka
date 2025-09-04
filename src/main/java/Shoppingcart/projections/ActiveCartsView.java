package Shoppingcart.projections;

import java.time.Instant;

import Shoppingcart.application.ShoppingCartEntity;
import Shoppingcart.domain.ShoppingCartEvent;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.TableUpdater.Effect;
import akka.javasdk.view.View;
import akka.javasdk.view.View.QueryEffect;

@ComponentId("active-carts")
public class ActiveCartsView extends View {

  @Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
  public static class ActiveCartsUpdater extends TableUpdater<ActiveCartEntry> {

    public Effect<ActiveCartEntry> onEvent(ShoppingCartEvent event) {
      long now = Instant.now().toEpochMilli();
      return switch (event) {
        case ShoppingCartEvent.ItemAdded e -> effects()
            .updateRow(new ActiveCartEntry(e.cartId(), now));
        case ShoppingCartEvent.ItemRemoved e -> effects()
            .updateRow(new ActiveCartEntry(e.cartId(), now));
        case ShoppingCartEvent.CartCheckedOut e -> effects().deleteRow();
      };
    }
  }

  @Query("SELECT * as activecarts FROM active_carts ORDER BY lastUpdated DESC")
  public QueryEffect<ActiveCartEntries> listActiveCarts() {
    return queryResult();
  }

  @Query("SELECT * as activecarts FROM active_carts WHERE cartId = :cartId")
  public QueryEffect<ActiveCartEntries> getActiveCart(String cartId) {
    return queryResult();
  }
}
