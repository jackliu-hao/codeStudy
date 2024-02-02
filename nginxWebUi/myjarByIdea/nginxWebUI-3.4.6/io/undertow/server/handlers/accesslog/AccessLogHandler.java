package io.undertow.server.handlers.accesslog;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.attribute.SubstituteEmptyWrapper;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AccessLogHandler implements HttpHandler {
   private final HttpHandler next;
   private final AccessLogReceiver accessLogReceiver;
   private final String formatString;
   private final ExchangeAttribute tokens;
   private final ExchangeCompletionListener exchangeCompletionListener;
   private final Predicate predicate;

   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ClassLoader classLoader) {
      this(next, accessLogReceiver, formatString, classLoader, Predicates.truePredicate());
   }

   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ClassLoader classLoader, Predicate predicate) {
      this.exchangeCompletionListener = new AccessLogCompletionListener();
      this.next = next;
      this.accessLogReceiver = accessLogReceiver;
      this.predicate = predicate;
      this.formatString = handleCommonNames(formatString);
      this.tokens = ExchangeAttributes.parser(classLoader, new SubstituteEmptyWrapper("-")).parse(this.formatString);
   }

   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ExchangeAttribute attribute) {
      this(next, accessLogReceiver, formatString, attribute, Predicates.truePredicate());
   }

   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ExchangeAttribute attribute, Predicate predicate) {
      this.exchangeCompletionListener = new AccessLogCompletionListener();
      this.next = next;
      this.accessLogReceiver = accessLogReceiver;
      this.predicate = predicate;
      this.formatString = handleCommonNames(formatString);
      this.tokens = attribute;
   }

   private static String handleCommonNames(String formatString) {
      if (formatString.equals("common")) {
         return "%h %l %u %t \"%r\" %s %b";
      } else if (formatString.equals("combined")) {
         return "%h %l %u %t \"%r\" %s %b \"%{i,Referer}\" \"%{i,User-Agent}\"";
      } else if (formatString.equals("commonobf")) {
         return "%o %l %u %t \"%r\" %s %b";
      } else {
         return formatString.equals("combinedobf") ? "%o %l %u %t \"%r\" %s %b \"%{i,Referer}\" \"%{i,User-Agent}\"" : formatString;
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addExchangeCompleteListener(this.exchangeCompletionListener);
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "AccessLogHandler{formatString='" + this.formatString + '\'' + '}';
   }

   private static class Wrapper implements HandlerWrapper {
      private final String format;
      private final String category;

      private Wrapper(String format, String category) {
         this.format = format;
         this.category = category;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return this.category != null && !this.category.trim().isEmpty() ? new AccessLogHandler(handler, new JBossLoggingAccessLogReceiver(this.category), this.format, Wrapper.class.getClassLoader()) : new AccessLogHandler(handler, new JBossLoggingAccessLogReceiver(), this.format, Wrapper.class.getClassLoader());
      }

      // $FF: synthetic method
      Wrapper(String x0, String x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "access-log";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("format", String.class);
         params.put("category", String.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("format");
      }

      public String defaultParameter() {
         return "format";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((String)config.get("format"), (String)config.get("category"));
      }
   }

   private class AccessLogCompletionListener implements ExchangeCompletionListener {
      private AccessLogCompletionListener() {
      }

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         try {
            if (AccessLogHandler.this.predicate == null || AccessLogHandler.this.predicate.resolve(exchange)) {
               AccessLogHandler.this.accessLogReceiver.logMessage(AccessLogHandler.this.tokens.readAttribute(exchange));
            }
         } finally {
            nextListener.proceed();
         }

      }

      // $FF: synthetic method
      AccessLogCompletionListener(Object x1) {
         this();
      }
   }
}
