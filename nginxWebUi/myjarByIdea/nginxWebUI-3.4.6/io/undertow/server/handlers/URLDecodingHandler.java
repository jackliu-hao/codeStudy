package io.undertow.server.handlers;

import io.undertow.UndertowOptions;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.AttachmentKey;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.URLUtils;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class URLDecodingHandler implements HttpHandler {
   private static final ThreadLocal<StringBuilder> DECODING_BUFFER_CACHE = ThreadLocal.withInitial(StringBuilder::new);
   private static final AttachmentKey<Object> ALREADY_DECODED = AttachmentKey.create(Object.class);
   private final HttpHandler next;
   private final String charset;

   public URLDecodingHandler(HttpHandler next, String charset) {
      this.next = next;
      this.charset = charset;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (shouldDecode(exchange)) {
         StringBuilder sb = getStringBuilderForDecoding(exchange);
         decodePath(exchange, this.charset, sb);
         decodeQueryString(exchange, this.charset, sb);
         decodePathTemplateMatch(exchange, this.charset, sb);
      }

      this.next.handleRequest(exchange);
   }

   private static boolean shouldDecode(HttpServerExchange exchange) {
      return !exchange.getConnection().getUndertowOptions().get(UndertowOptions.DECODE_URL, true) && exchange.putAttachment(ALREADY_DECODED, Boolean.TRUE) == null;
   }

   private static void decodePath(HttpServerExchange exchange, String charset, StringBuilder sb) {
      boolean decodeSlash = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
      exchange.setRequestPath(URLUtils.decode(exchange.getRequestPath(), charset, decodeSlash, false, sb));
      exchange.setRelativePath(URLUtils.decode(exchange.getRelativePath(), charset, decodeSlash, false, sb));
      exchange.setResolvedPath(URLUtils.decode(exchange.getResolvedPath(), charset, decodeSlash, false, sb));
   }

   private static void decodeQueryString(HttpServerExchange exchange, String charset, StringBuilder sb) {
      if (!exchange.getQueryString().isEmpty()) {
         TreeMap<String, Deque<String>> newParams = new TreeMap();
         Iterator var4 = exchange.getQueryParameters().entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, Deque<String>> param = (Map.Entry)var4.next();
            Deque<String> newValues = new ArrayDeque(((Deque)param.getValue()).size());
            Iterator var7 = ((Deque)param.getValue()).iterator();

            while(var7.hasNext()) {
               String val = (String)var7.next();
               newValues.add(URLUtils.decode(val, charset, true, true, sb));
            }

            newParams.put(URLUtils.decode((String)param.getKey(), charset, true, true, sb), newValues);
         }

         exchange.getQueryParameters().clear();
         exchange.getQueryParameters().putAll(newParams);
      }

   }

   private static void decodePathTemplateMatch(HttpServerExchange exchange, String charset, StringBuilder sb) {
      PathTemplateMatch pathTemplateMatch = (PathTemplateMatch)exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
      if (pathTemplateMatch != null) {
         Map<String, String> parameters = pathTemplateMatch.getParameters();
         if (parameters != null) {
            Iterator var5 = parameters.entrySet().iterator();

            while(var5.hasNext()) {
               Map.Entry<String, String> entry = (Map.Entry)var5.next();
               entry.setValue(URLUtils.decode((String)entry.getValue(), charset, true, false, sb));
            }
         }
      }

   }

   private static StringBuilder getStringBuilderForDecoding(HttpServerExchange exchange) {
      return exchange.isInIoThread() ? (StringBuilder)DECODING_BUFFER_CACHE.get() : new StringBuilder();
   }

   public String toString() {
      return "url-decoding( " + this.charset + " )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final String charset;

      private Wrapper(String charset) {
         this.charset = charset;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new URLDecodingHandler(handler, this.charset);
      }

      // $FF: synthetic method
      Wrapper(String x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "url-decoding";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("charset", String.class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("charset");
      }

      public String defaultParameter() {
         return "charset";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper(config.get("charset").toString());
      }
   }
}
