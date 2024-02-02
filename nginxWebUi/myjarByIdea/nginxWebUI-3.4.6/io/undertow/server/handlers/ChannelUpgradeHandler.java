package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.StreamConnection;

public final class ChannelUpgradeHandler implements HttpHandler {
   private final CopyOnWriteMap<String, List<Holder>> handlers = new CopyOnWriteMap();
   private volatile HttpHandler nonUpgradeHandler;

   public ChannelUpgradeHandler() {
      this.nonUpgradeHandler = ResponseCodeHandler.HANDLE_404;
   }

   public synchronized void addProtocol(String productString, ChannelListener<? super StreamConnection> openListener, HttpUpgradeHandshake handshake) {
      this.addProtocol(productString, (HttpUpgradeListener)null, openListener, handshake);
   }

   public synchronized void addProtocol(String productString, HttpUpgradeListener openListener, HttpUpgradeHandshake handshake) {
      this.addProtocol(productString, openListener, (ChannelListener)null, handshake);
   }

   private synchronized void addProtocol(String productString, HttpUpgradeListener openListener, final ChannelListener<? super StreamConnection> channelListener, HttpUpgradeHandshake handshake) {
      if (productString == null) {
         throw new IllegalArgumentException("productString is null");
      } else if (openListener == null && channelListener == null) {
         throw new IllegalArgumentException("openListener is null");
      } else {
         if (openListener == null) {
            openListener = new HttpUpgradeListener() {
               public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
                  ChannelListeners.invokeChannelListener(streamConnection, channelListener);
               }
            };
         }

         List<Holder> list = (List)this.handlers.get(productString);
         if (list == null) {
            this.handlers.put(productString, list = new CopyOnWriteArrayList());
         }

         ((List)list).add(new Holder(openListener, handshake, channelListener));
      }
   }

   public void addProtocol(String productString, ChannelListener<? super StreamConnection> openListener) {
      this.addProtocol(productString, (ChannelListener)openListener, (HttpUpgradeHandshake)null);
   }

   public void addProtocol(String productString, HttpUpgradeListener openListener) {
      this.addProtocol(productString, (HttpUpgradeListener)openListener, (HttpUpgradeHandshake)null);
   }

   public synchronized void removeProtocol(String productString) {
      this.handlers.remove(productString);
   }

   public synchronized void removeProtocol(String productString, ChannelListener<? super StreamConnection> openListener) {
      List<Holder> holders = (List)this.handlers.get(productString);
      if (holders != null) {
         Iterator<Holder> it = holders.iterator();

         while(it.hasNext()) {
            Holder holder = (Holder)it.next();
            if (holder.channelListener == openListener) {
               holders.remove(holder);
               break;
            }
         }

         if (holders.isEmpty()) {
            this.handlers.remove(productString);
         }

      }
   }

   public synchronized void removeProtocol(String productString, HttpUpgradeListener upgradeListener) {
      List<Holder> holders = (List)this.handlers.get(productString);
      if (holders != null) {
         Iterator<Holder> it = holders.iterator();

         while(it.hasNext()) {
            Holder holder = (Holder)it.next();
            if (holder.listener == upgradeListener) {
               holders.remove(holder);
               break;
            }
         }

         if (holders.isEmpty()) {
            this.handlers.remove(productString);
         }

      }
   }

   public HttpHandler getNonUpgradeHandler() {
      return this.nonUpgradeHandler;
   }

   public ChannelUpgradeHandler setNonUpgradeHandler(HttpHandler nonUpgradeHandler) {
      Handlers.handlerNotNull(nonUpgradeHandler);
      this.nonUpgradeHandler = nonUpgradeHandler;
      return this;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      List<String> upgradeStrings = exchange.getRequestHeaders().get(Headers.UPGRADE);
      if (upgradeStrings != null && exchange.getRequestMethod().equals(Methods.GET)) {
         Iterator var3 = upgradeStrings.iterator();

         label37:
         while(true) {
            String string;
            List holders;
            do {
               if (!var3.hasNext()) {
                  break label37;
               }

               string = (String)var3.next();
               holders = (List)this.handlers.get(string);
            } while(holders == null);

            Iterator var6 = holders.iterator();

            while(var6.hasNext()) {
               Holder holder = (Holder)var6.next();
               HttpUpgradeListener listener = holder.listener;
               if (holder.handshake == null || holder.handshake.handleUpgrade(exchange)) {
                  exchange.upgradeChannel(string, listener);
                  exchange.endExchange();
                  return;
               }
            }
         }
      }

      this.nonUpgradeHandler.handleRequest(exchange);
   }

   private static final class Holder {
      final HttpUpgradeListener listener;
      final HttpUpgradeHandshake handshake;
      final ChannelListener<? super StreamConnection> channelListener;

      private Holder(HttpUpgradeListener listener, HttpUpgradeHandshake handshake, ChannelListener<? super StreamConnection> channelListener) {
         this.listener = listener;
         this.handshake = handshake;
         this.channelListener = channelListener;
      }

      // $FF: synthetic method
      Holder(HttpUpgradeListener x0, HttpUpgradeHandshake x1, ChannelListener x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
