package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.attribute.StoredResponse;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.LocaleUtils;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestDumpingHandler implements HttpHandler {
   private final HttpHandler next;

   public RequestDumpingHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      final StringBuilder sb = new StringBuilder();
      final SecurityContext sc = exchange.getSecurityContext();
      sb.append("\n----------------------------REQUEST---------------------------\n");
      sb.append("               URI=" + exchange.getRequestURI() + "\n");
      sb.append(" characterEncoding=" + exchange.getRequestHeaders().get(Headers.CONTENT_ENCODING) + "\n");
      sb.append("     contentLength=" + exchange.getRequestContentLength() + "\n");
      sb.append("       contentType=" + exchange.getRequestHeaders().get(Headers.CONTENT_TYPE) + "\n");
      if (sc != null) {
         if (sc.isAuthenticated()) {
            sb.append("          authType=" + sc.getMechanismName() + "\n");
            sb.append("         principle=" + sc.getAuthenticatedAccount().getPrincipal() + "\n");
         } else {
            sb.append("          authType=none\n");
         }
      }

      Iterator var4 = exchange.requestCookies().iterator();

      while(var4.hasNext()) {
         Cookie cookie = (Cookie)var4.next();
         sb.append("            cookie=" + cookie.getName() + "=" + cookie.getValue() + "\n");
      }

      var4 = exchange.getRequestHeaders().iterator();

      String pname;
      while(var4.hasNext()) {
         HeaderValues header = (HeaderValues)var4.next();
         Iterator var6 = header.iterator();

         while(var6.hasNext()) {
            pname = (String)var6.next();
            sb.append("            header=" + header.getHeaderName() + "=" + pname + "\n");
         }
      }

      sb.append("            locale=" + LocaleUtils.getLocalesFromHeader((List)exchange.getRequestHeaders().get(Headers.ACCEPT_LANGUAGE)) + "\n");
      sb.append("            method=" + exchange.getRequestMethod() + "\n");
      Map<String, Deque<String>> pnames = exchange.getQueryParameters();
      Iterator var11 = pnames.entrySet().iterator();

      while(var11.hasNext()) {
         Map.Entry<String, Deque<String>> entry = (Map.Entry)var11.next();
         pname = (String)entry.getKey();
         Iterator<String> pvalues = ((Deque)entry.getValue()).iterator();
         sb.append("         parameter=");
         sb.append(pname);
         sb.append('=');

         while(pvalues.hasNext()) {
            sb.append((String)pvalues.next());
            if (pvalues.hasNext()) {
               sb.append(", ");
            }
         }

         sb.append("\n");
      }

      sb.append("          protocol=" + exchange.getProtocol() + "\n");
      sb.append("       queryString=" + exchange.getQueryString() + "\n");
      sb.append("        remoteAddr=" + exchange.getSourceAddress() + "\n");
      sb.append("        remoteHost=" + exchange.getSourceAddress().getHostName() + "\n");
      sb.append("            scheme=" + exchange.getRequestScheme() + "\n");
      sb.append("              host=" + exchange.getRequestHeaders().getFirst(Headers.HOST) + "\n");
      sb.append("        serverPort=" + exchange.getDestinationAddress().getPort() + "\n");
      sb.append("          isSecure=" + exchange.isSecure() + "\n");
      exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
         public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
            RequestDumpingHandler.this.dumpRequestBody(exchange, sb);
            sb.append("--------------------------RESPONSE--------------------------\n");
            if (sc != null) {
               if (sc.isAuthenticated()) {
                  sb.append("          authType=" + sc.getMechanismName() + "\n");
                  sb.append("         principle=" + sc.getAuthenticatedAccount().getPrincipal() + "\n");
               } else {
                  sb.append("          authType=none\n");
               }
            }

            sb.append("     contentLength=" + exchange.getResponseContentLength() + "\n");
            sb.append("       contentType=" + exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE) + "\n");
            Iterator var3 = exchange.responseCookies().iterator();

            while(var3.hasNext()) {
               Cookie cookie = (Cookie)var3.next();
               sb.append("            cookie=" + cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain() + "; path=" + cookie.getPath() + "\n");
            }

            var3 = exchange.getResponseHeaders().iterator();

            while(var3.hasNext()) {
               HeaderValues header = (HeaderValues)var3.next();
               Iterator var5 = header.iterator();

               while(var5.hasNext()) {
                  String value = (String)var5.next();
                  sb.append("            header=" + header.getHeaderName() + "=" + value + "\n");
               }
            }

            sb.append("            status=" + exchange.getStatusCode() + "\n");
            String storedResponse = StoredResponse.INSTANCE.readAttribute(exchange);
            if (storedResponse != null) {
               sb.append("body=\n");
               sb.append(storedResponse);
            }

            sb.append("\n==============================================================");
            nextListener.proceed();
            UndertowLogger.REQUEST_DUMPER_LOGGER.info(sb.toString());
         }
      });
      this.next.handleRequest(exchange);
   }

   private void dumpRequestBody(HttpServerExchange exchange, StringBuilder sb) {
      try {
         FormData formData = (FormData)exchange.getAttachment(FormDataParser.FORM_DATA);
         if (formData != null) {
            sb.append("body=\n");
            Iterator var4 = formData.iterator();

            label42:
            while(var4.hasNext()) {
               String formField = (String)var4.next();
               Deque<FormData.FormValue> formValues = formData.get(formField);
               sb.append(formField).append("=");
               Iterator var7 = formValues.iterator();

               while(true) {
                  FormData.FormValue formValue;
                  do {
                     if (!var7.hasNext()) {
                        continue label42;
                     }

                     formValue = (FormData.FormValue)var7.next();
                     sb.append(formValue.isFileItem() ? "[file-content]" : formValue.getValue());
                     sb.append("\n");
                  } while(formValue.getHeaders() == null);

                  sb.append("headers=\n");
                  Iterator var9 = formValue.getHeaders().iterator();

                  while(var9.hasNext()) {
                     HeaderValues header = (HeaderValues)var9.next();
                     sb.append("\t").append(header.getHeaderName()).append("=").append(header.getFirst()).append("\n");
                  }
               }
            }
         }

      } catch (Exception var11) {
         throw new RuntimeException(var11);
      }
   }

   public String toString() {
      return "dump-request()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new RequestDumpingHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "dump-request";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper();
      }
   }
}
