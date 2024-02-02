package io.undertow.util;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Cookies {
   public static final String DOMAIN = "$Domain";
   public static final String VERSION = "$Version";
   public static final String PATH = "$Path";

   public static Cookie parseSetCookieHeader(String headerValue) {
      String key = null;
      CookieImpl cookie = null;
      int state = 0;
      int current = 0;

      for(int i = 0; i < headerValue.length(); ++i) {
         char c = headerValue.charAt(i);
         switch (state) {
            case 0:
               if (c == '=') {
                  key = headerValue.substring(current, i);
                  current = i + 1;
                  state = 1;
               } else if ((c == ';' || c == ' ') && current == i) {
                  ++current;
               } else if (c == ';') {
                  if (cookie == null) {
                     throw UndertowMessages.MESSAGES.couldNotParseCookie(headerValue);
                  }

                  handleValue(cookie, headerValue.substring(current, i), (String)null);
                  current = i + 1;
               }
               break;
            case 1:
               if (c == ';') {
                  if (cookie == null) {
                     cookie = new CookieImpl(key, headerValue.substring(current, i));
                  } else {
                     handleValue(cookie, key, headerValue.substring(current, i));
                  }

                  state = 0;
                  current = i + 1;
                  key = null;
               } else if (c == '"' && current == i) {
                  ++current;
                  state = 2;
               }
               break;
            case 2:
               if (c == '"') {
                  if (cookie == null) {
                     cookie = new CookieImpl(key, headerValue.substring(current, i));
                  } else {
                     handleValue(cookie, key, headerValue.substring(current, i));
                  }

                  state = 0;
                  current = i + 1;
                  key = null;
               }
         }
      }

      if (key == null) {
         if (current != headerValue.length()) {
            handleValue(cookie, headerValue.substring(current, headerValue.length()), (String)null);
         }
      } else if (current != headerValue.length()) {
         if (cookie == null) {
            cookie = new CookieImpl(key, headerValue.substring(current, headerValue.length()));
         } else {
            handleValue(cookie, key, headerValue.substring(current, headerValue.length()));
         }
      } else {
         handleValue(cookie, key, (String)null);
      }

      return cookie;
   }

   private static void handleValue(CookieImpl cookie, String key, String value) {
      if (key != null) {
         if (key.equalsIgnoreCase("path")) {
            cookie.setPath(value);
         } else if (key.equalsIgnoreCase("domain")) {
            cookie.setDomain(value);
         } else if (key.equalsIgnoreCase("max-age")) {
            cookie.setMaxAge(Integer.parseInt(value));
         } else if (key.equalsIgnoreCase("expires")) {
            cookie.setExpires(DateUtils.parseDate(value));
         } else if (key.equalsIgnoreCase("discard")) {
            cookie.setDiscard(true);
         } else if (key.equalsIgnoreCase("secure")) {
            cookie.setSecure(true);
         } else if (key.equalsIgnoreCase("httpOnly")) {
            cookie.setHttpOnly(true);
         } else if (key.equalsIgnoreCase("version")) {
            cookie.setVersion(Integer.parseInt(value));
         } else if (key.equalsIgnoreCase("comment")) {
            cookie.setComment(value);
         } else if (key.equalsIgnoreCase("samesite")) {
            cookie.setSameSite(true);
            cookie.setSameSiteMode(value);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies) {
      return parseRequestCookies(maxCookies, allowEqualInValue, cookies, LegacyCookieSupport.COMMA_IS_SEPARATOR);
   }

   public static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies) {
      parseRequestCookies(maxCookies, allowEqualInValue, cookies, parsedCookies, LegacyCookieSupport.COMMA_IS_SEPARATOR);
   }

   /** @deprecated */
   @Deprecated
   static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, boolean commaIsSeperator) {
      return parseRequestCookies(maxCookies, allowEqualInValue, cookies, commaIsSeperator, LegacyCookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0);
   }

   static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies, boolean commaIsSeperator) {
      parseRequestCookies(maxCookies, allowEqualInValue, cookies, parsedCookies, commaIsSeperator, LegacyCookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0);
   }

   static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
      if (cookies == null) {
         return new TreeMap();
      } else {
         Set<Cookie> parsedCookies = new HashSet();
         Iterator var6 = cookies.iterator();

         while(var6.hasNext()) {
            String cookie = (String)var6.next();
            parseCookie(cookie, parsedCookies, maxCookies, allowEqualInValue, commaIsSeperator, allowHttpSepartorsV0);
         }

         Map<String, Cookie> retVal = new TreeMap();
         Iterator var10 = parsedCookies.iterator();

         while(var10.hasNext()) {
            Cookie cookie = (Cookie)var10.next();
            retVal.put(cookie.getName(), cookie);
         }

         return retVal;
      }
   }

   static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
      if (cookies != null) {
         Iterator var6 = cookies.iterator();

         while(var6.hasNext()) {
            String cookie = (String)var6.next();
            parseCookie(cookie, parsedCookies, maxCookies, allowEqualInValue, commaIsSeperator, allowHttpSepartorsV0);
         }
      }

   }

   private static void parseCookie(String cookie, Set<Cookie> parsedCookies, int maxCookies, boolean allowEqualInValue, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
      int state = 0;
      String name = null;
      int start = 0;
      boolean containsEscapedQuotes = false;
      int cookieCount = parsedCookies.size();
      Map<String, String> cookies = new HashMap();
      Map<String, String> additional = new HashMap();

      for(int i = 0; i < cookie.length(); ++i) {
         char c = cookie.charAt(i);
         switch (state) {
            case 0:
               if (c == ' ' || c == '\t' || c == ';') {
                  start = i + 1;
                  break;
               } else {
                  state = 1;
               }
            case 1:
               if (c == '=') {
                  name = cookie.substring(start, i);
                  start = i + 1;
                  state = 2;
               } else {
                  if (c != ';' && (!commaIsSeperator || c != ',')) {
                     continue;
                  }

                  if (name != null) {
                     cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
                  } else if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
                     UndertowLogger.REQUEST_LOGGER.trace("Ignoring invalid cookies in header " + cookie);
                  }

                  state = 0;
                  start = i + 1;
               }
               break;
            case 2:
               if (c == ';' || commaIsSeperator && c == ',') {
                  cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
                  state = 0;
                  start = i + 1;
               } else if (c == '"' && start == i) {
                  containsEscapedQuotes = false;
                  state = 3;
                  start = i + 1;
               } else if (c == '=') {
                  if (!allowEqualInValue && !allowHttpSepartorsV0) {
                     cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
                     state = 4;
                     start = i + 1;
                  }
               } else if (c != ':' && !allowHttpSepartorsV0 && LegacyCookieSupport.isHttpSeparator(c)) {
                  cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
                  state = 4;
                  start = i + 1;
               }
               break;
            case 3:
               if (c == '"') {
                  cookieCount = createCookie(name, containsEscapedQuotes ? unescapeDoubleQuotes(cookie.substring(start, i)) : cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
                  state = 0;
                  start = i + 1;
               }

               if (c == '\\' && i + 1 < cookie.length() && cookie.charAt(i + 1) == '"' && i + 2 != cookie.length() && (i + 2 >= cookie.length() || cookie.charAt(i + 2) != ';' && (!commaIsSeperator || cookie.charAt(i + 2) != ','))) {
                  ++i;
                  containsEscapedQuotes = true;
               }
               break;
            case 4:
               if (c == ';' || commaIsSeperator && c == ',') {
                  state = 0;
               }

               start = i + 1;
         }
      }

      if (state == 2) {
         createCookie(name, cookie.substring(start), maxCookies, cookieCount, cookies, additional);
      }

      CookieImpl c;
      for(Iterator var19 = cookies.entrySet().iterator(); var19.hasNext(); parsedCookies.add(c)) {
         Map.Entry<String, String> entry = (Map.Entry)var19.next();
         c = new CookieImpl((String)entry.getKey(), (String)entry.getValue());
         String domain = (String)additional.get("$Domain");
         if (domain != null) {
            c.setDomain(domain);
         }

         String version = (String)additional.get("$Version");
         if (version != null) {
            c.setVersion(Integer.parseInt(version));
         }

         String path = (String)additional.get("$Path");
         if (path != null) {
            c.setPath(path);
         }
      }

   }

   private static int createCookie(String name, String value, int maxCookies, int cookieCount, Map<String, String> cookies, Map<String, String> additional) {
      if (!name.isEmpty() && name.charAt(0) == '$') {
         if (additional.containsKey(name)) {
            return cookieCount;
         } else {
            additional.put(name, value);
            return cookieCount;
         }
      } else if (cookieCount == maxCookies) {
         throw UndertowMessages.MESSAGES.tooManyCookies(maxCookies);
      } else if (cookies.containsKey(name)) {
         return cookieCount;
      } else {
         cookies.put(name, value);
         ++cookieCount;
         return cookieCount;
      }
   }

   private static String unescapeDoubleQuotes(String value) {
      if (value != null && !value.isEmpty()) {
         char[] tmp = new char[value.length()];
         int dest = 0;

         for(int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\\' && i + 1 < value.length() && value.charAt(i + 1) == '"') {
               ++i;
            }

            tmp[dest] = value.charAt(i);
            ++dest;
         }

         return new String(tmp, 0, dest);
      } else {
         return value;
      }
   }

   private Cookies() {
   }
}
