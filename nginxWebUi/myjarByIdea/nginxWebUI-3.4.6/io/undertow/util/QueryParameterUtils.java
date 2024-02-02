package io.undertow.util;

import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xnio.OptionMap;

public class QueryParameterUtils {
   private QueryParameterUtils() {
   }

   public static String buildQueryString(Map<String, Deque<String>> params) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      Iterator var3 = params.entrySet().iterator();

      while(true) {
         while(var3.hasNext()) {
            Map.Entry<String, Deque<String>> entry = (Map.Entry)var3.next();
            if (((Deque)entry.getValue()).isEmpty()) {
               if (first) {
                  first = false;
               } else {
                  sb.append('&');
               }

               sb.append((String)entry.getKey());
               sb.append('=');
            } else {
               Iterator var5 = ((Deque)entry.getValue()).iterator();

               while(var5.hasNext()) {
                  String val = (String)var5.next();
                  if (first) {
                     first = false;
                  } else {
                     sb.append('&');
                  }

                  sb.append((String)entry.getKey());
                  sb.append('=');
                  sb.append(val);
               }
            }
         }

         return sb.toString();
      }
   }

   /** @deprecated */
   @Deprecated
   public static Map<String, Deque<String>> parseQueryString(String newQueryString) {
      return parseQueryString(newQueryString, (String)null);
   }

   public static Map<String, Deque<String>> parseQueryString(String newQueryString, String encoding) {
      Map<String, Deque<String>> newQueryParameters = new LinkedHashMap();
      int startPos = 0;
      int equalPos = -1;
      boolean needsDecode = false;

      for(int i = 0; i < newQueryString.length(); ++i) {
         char c = newQueryString.charAt(i);
         if (c == '=' && equalPos == -1) {
            equalPos = i;
         } else if (c == '&') {
            handleQueryParameter(newQueryString, newQueryParameters, startPos, equalPos, i, encoding, needsDecode);
            needsDecode = false;
            startPos = i + 1;
            equalPos = -1;
         } else if ((c == '%' || c == '+') && encoding != null) {
            needsDecode = true;
         }
      }

      if (startPos != newQueryString.length()) {
         handleQueryParameter(newQueryString, newQueryParameters, startPos, equalPos, newQueryString.length(), encoding, needsDecode);
      }

      return newQueryParameters;
   }

   private static void handleQueryParameter(String newQueryString, Map<String, Deque<String>> newQueryParameters, int startPos, int equalPos, int i, String encoding, boolean needsDecode) {
      String value = "";
      String key;
      if (equalPos == -1) {
         key = decodeParam(newQueryString, startPos, i, encoding, needsDecode);
      } else {
         key = decodeParam(newQueryString, startPos, equalPos, encoding, needsDecode);
         value = decodeParam(newQueryString, equalPos + 1, i, encoding, needsDecode);
      }

      Deque<String> queue = (Deque)newQueryParameters.get(key);
      if (queue == null) {
         newQueryParameters.put(key, queue = new ArrayDeque(1));
      }

      if (value != null) {
         ((Deque)queue).add(value);
      }

   }

   private static String decodeParam(String newQueryString, int startPos, int equalPos, String encoding, boolean needsDecode) {
      String key;
      if (needsDecode) {
         try {
            key = URLDecoder.decode(newQueryString.substring(startPos, equalPos), encoding);
         } catch (UnsupportedEncodingException var7) {
            key = newQueryString.substring(startPos, equalPos);
         }
      } else {
         key = newQueryString.substring(startPos, equalPos);
      }

      return key;
   }

   /** @deprecated */
   @Deprecated
   public static Map<String, Deque<String>> mergeQueryParametersWithNewQueryString(Map<String, Deque<String>> queryParameters, String newQueryString) {
      return mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, StandardCharsets.UTF_8.name());
   }

   public static Map<String, Deque<String>> mergeQueryParametersWithNewQueryString(Map<String, Deque<String>> queryParameters, String newQueryString, String encoding) {
      Map<String, Deque<String>> newQueryParameters = parseQueryString(newQueryString, encoding);
      Iterator var4 = queryParameters.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<String, Deque<String>> entry = (Map.Entry)var4.next();
         if (!newQueryParameters.containsKey(entry.getKey())) {
            newQueryParameters.put(entry.getKey(), new ArrayDeque((Collection)entry.getValue()));
         } else {
            ((Deque)newQueryParameters.get(entry.getKey())).addAll((Collection)entry.getValue());
         }
      }

      return newQueryParameters;
   }

   public static String getQueryParamEncoding(HttpServerExchange exchange) {
      String encoding = null;
      OptionMap undertowOptions = exchange.getConnection().getUndertowOptions();
      if (undertowOptions.get(UndertowOptions.DECODE_URL, true)) {
         encoding = (String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
      }

      return encoding;
   }
}
