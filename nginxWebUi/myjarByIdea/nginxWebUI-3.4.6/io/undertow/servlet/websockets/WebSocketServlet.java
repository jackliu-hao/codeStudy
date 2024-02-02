package io.undertow.servlet.websockets;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xnio.StreamConnection;

public class WebSocketServlet extends HttpServlet {
   public static final String SESSION_HANDLER = "io.undertow.handler";
   private final List<Handshake> handshakes;
   private WebSocketConnectionCallback callback;
   private Set<WebSocketChannel> peerConnections;

   public WebSocketServlet() {
      this.handshakes = this.handshakes();
   }

   public WebSocketServlet(WebSocketConnectionCallback callback) {
      this.callback = callback;
      this.handshakes = this.handshakes();
   }

   public void init(ServletConfig config) throws ServletException {
      super.init(config);
      this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());

      try {
         String sessionHandler = config.getInitParameter("io.undertow.handler");
         if (sessionHandler != null) {
            Class<?> clazz = Class.forName(sessionHandler, true, Thread.currentThread().getContextClassLoader());
            Object handler = clazz.newInstance();
            this.callback = (WebSocketConnectionCallback)handler;
         }
      } catch (ClassNotFoundException var5) {
         throw new ServletException(var5);
      } catch (InstantiationException var6) {
         throw new ServletException(var6);
      } catch (IllegalAccessException var7) {
         throw new ServletException(var7);
      }

      if (this.callback == null) {
         throw UndertowServletMessages.MESSAGES.noWebSocketHandler();
      }
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      final ServletWebSocketHttpExchange facade = new ServletWebSocketHttpExchange(req, resp, this.peerConnections);
      final Handshake handshaker = null;
      Iterator var5 = this.handshakes.iterator();

      while(var5.hasNext()) {
         Handshake method = (Handshake)var5.next();
         if (method.matches(facade)) {
            handshaker = method;
            break;
         }
      }

      if (handshaker == null) {
         UndertowLogger.REQUEST_LOGGER.debug("Could not find hand shaker for web socket request");
         resp.sendError(400);
      } else {
         facade.upgradeChannel(new HttpUpgradeListener() {
            public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
               WebSocketChannel channel = handshaker.createChannel(facade, streamConnection, facade.getBufferPool());
               WebSocketServlet.this.peerConnections.add(channel);
               WebSocketServlet.this.callback.onConnect(facade, channel);
            }
         });
         handshaker.handshake(facade);
      }
   }

   protected List<Handshake> handshakes() {
      List<Handshake> handshakes = new ArrayList();
      handshakes.add(new Hybi13Handshake());
      handshakes.add(new Hybi08Handshake());
      handshakes.add(new Hybi07Handshake());
      return handshakes;
   }
}
