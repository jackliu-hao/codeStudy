package io.undertow.server;

import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.ConduitFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xnio.conduits.StreamSinkConduit;

public class JvmRouteHandler implements HttpHandler {
   private final HttpHandler next;
   private final String sessionCookieName;
   private final String jvmRoute;
   private final JvmRouteWrapper wrapper = new JvmRouteWrapper();

   public JvmRouteHandler(HttpHandler next, String sessionCookieName, String jvmRoute) {
      this.next = next;
      this.sessionCookieName = sessionCookieName;
      this.jvmRoute = jvmRoute;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      Iterator var2 = exchange.requestCookies().iterator();

      while(var2.hasNext()) {
         Cookie cookie = (Cookie)var2.next();
         if (this.sessionCookieName.equals(cookie.getName())) {
            int part = cookie.getValue().indexOf(46);
            if (part != -1) {
               cookie.setValue(cookie.getValue().substring(0, part));
            }
         }
      }

      exchange.addResponseWrapper(this.wrapper);
      this.next.handleRequest(exchange);
   }

   private static class Wrapper implements HandlerWrapper {
      private final String value;
      private final String sessionCookieName;

      private Wrapper(String value, String sessionCookieName) {
         this.value = value;
         this.sessionCookieName = sessionCookieName;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new JvmRouteHandler(handler, this.sessionCookieName, this.value);
      }

      // $FF: synthetic method
      Wrapper(String x0, String x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "jvm-route";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", String.class);
         params.put("session-cookie-name", String.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         String sessionCookieName = (String)config.get("session-cookie-name");
         return new Wrapper((String)config.get("value"), sessionCookieName == null ? "JSESSIONID" : sessionCookieName);
      }
   }

   private class JvmRouteWrapper implements ConduitWrapper<StreamSinkConduit> {
      private JvmRouteWrapper() {
      }

      public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
         Iterator var3 = exchange.responseCookies().iterator();

         while(var3.hasNext()) {
            Cookie cookie = (Cookie)var3.next();
            if (JvmRouteHandler.this.sessionCookieName.equals(cookie.getName())) {
               StringBuilder sb = new StringBuilder(cookie.getValue());
               sb.append('.');
               sb.append(JvmRouteHandler.this.jvmRoute);
               cookie.setValue(sb.toString());
            }
         }

         return (StreamSinkConduit)factory.create();
      }

      // $FF: synthetic method
      JvmRouteWrapper(Object x1) {
         this();
      }
   }
}
