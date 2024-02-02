package io.undertow.util;

import io.undertow.server.HttpServerExchange;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

public class RedirectBuilder {
   public static final String UTF_8;

   public static String redirect(HttpServerExchange exchange, String newRelativePath) {
      return redirect(exchange, newRelativePath, true);
   }

   public static String redirect(HttpServerExchange exchange, String newRelativePath, boolean includeParameters) {
      try {
         StringBuilder uri = new StringBuilder(exchange.getRequestScheme());
         uri.append("://");
         uri.append(exchange.getHostAndPort());
         uri.append(encodeUrlPart(exchange.getResolvedPath()));
         if (exchange.getResolvedPath().endsWith("/")) {
            if (newRelativePath.startsWith("/")) {
               uri.append(encodeUrlPart(newRelativePath.substring(1)));
            } else {
               uri.append(encodeUrlPart(newRelativePath));
            }
         } else {
            if (!newRelativePath.startsWith("/")) {
               uri.append('/');
            }

            uri.append(encodeUrlPart(newRelativePath));
         }

         if (includeParameters) {
            if (!exchange.getPathParameters().isEmpty()) {
               boolean first = true;
               uri.append(';');
               Iterator var5 = exchange.getPathParameters().entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry<String, Deque<String>> param = (Map.Entry)var5.next();
                  Iterator var7 = ((Deque)param.getValue()).iterator();

                  while(var7.hasNext()) {
                     String value = (String)var7.next();
                     if (first) {
                        first = false;
                     } else {
                        uri.append('&');
                     }

                     uri.append(URLEncoder.encode((String)param.getKey(), UTF_8));
                     uri.append('=');
                     uri.append(URLEncoder.encode(value, UTF_8));
                  }
               }
            }

            if (!exchange.getQueryString().isEmpty()) {
               uri.append('?');
               uri.append(exchange.getQueryString());
            }
         }

         return uri.toString();
      } catch (UnsupportedEncodingException var9) {
         throw new RuntimeException(var9);
      }
   }

   private static String encodeUrlPart(String part) throws UnsupportedEncodingException {
      int pos = 0;

      for(int i = 0; i < part.length(); ++i) {
         char c = part.charAt(i);
         if (c == '?') {
            break;
         }

         if (c == '/') {
            if (pos != i) {
               String original = part.substring(pos, i);
               String encoded = URLEncoder.encode(original, UTF_8);
               if (!encoded.equals(original)) {
                  return realEncode(part, pos);
               }
            }

            pos = i + 1;
         } else if (c == ' ') {
            return realEncode(part, pos);
         }
      }

      return part;
   }

   private static String realEncode(String part, int startPos) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      sb.append(part.substring(0, startPos));
      int pos = startPos;

      for(int i = startPos; i < part.length(); ++i) {
         char c = part.charAt(i);
         if (c == '?') {
            break;
         }

         if (c == '/' && pos != i) {
            String original = part.substring(pos, i);
            String encoded = URLEncoder.encode(original, UTF_8);
            sb.append(encoded);
            sb.append('/');
            pos = i + 1;
         }
      }

      String original = part.substring(pos);
      String encoded = URLEncoder.encode(original, UTF_8);
      sb.append(encoded);
      return sb.toString();
   }

   private RedirectBuilder() {
   }

   static {
      UTF_8 = StandardCharsets.UTF_8.name();
   }
}
