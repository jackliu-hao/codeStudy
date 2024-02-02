package io.undertow.server;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.LegacyCookieSupport;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.URLUtils;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;

public class Connectors {
   private static final boolean[] ALLOWED_TOKEN_CHARACTERS = new boolean[256];
   private static final boolean[] ALLOWED_SCHEME_CHARACTERS = new boolean[256];

   public static void flattenCookies(HttpServerExchange exchange) {
      boolean enableRfc6265Validation = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false);
      Iterator var2 = exchange.responseCookies().iterator();

      while(var2.hasNext()) {
         Cookie cookie = (Cookie)var2.next();
         exchange.getResponseHeaders().add(Headers.SET_COOKIE, getCookieString(cookie, enableRfc6265Validation));
      }

   }

   public static void addCookie(HttpServerExchange exchange, Cookie cookie) {
      boolean enableRfc6265Validation = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false);
      exchange.getResponseHeaders().add(Headers.SET_COOKIE, getCookieString(cookie, enableRfc6265Validation));
   }

   public static void ungetRequestBytes(HttpServerExchange exchange, PooledByteBuffer... buffers) {
      PooledByteBuffer[] existing = (PooledByteBuffer[])exchange.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
      PooledByteBuffer[] newArray;
      if (existing == null) {
         newArray = new PooledByteBuffer[buffers.length];
         System.arraycopy(buffers, 0, newArray, 0, buffers.length);
      } else {
         newArray = new PooledByteBuffer[existing.length + buffers.length];
         System.arraycopy(existing, 0, newArray, 0, existing.length);
         System.arraycopy(buffers, 0, newArray, existing.length, buffers.length);
      }

      exchange.putAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA, newArray);
      exchange.addExchangeCompleteListener(Connectors.BufferedRequestDataCleanupListener.INSTANCE);
   }

   public static void terminateRequest(HttpServerExchange exchange) {
      exchange.terminateRequest();
   }

   public static void terminateResponse(HttpServerExchange exchange) {
      exchange.terminateResponse();
   }

   public static void resetRequestChannel(HttpServerExchange exchange) {
      exchange.resetRequestChannel();
   }

   private static String getCookieString(Cookie var0, boolean var1) {
      // $FF: Couldn't be decompiled
   }

   public static void setRequestStartTime(HttpServerExchange exchange) {
      exchange.setRequestStartTime(System.nanoTime());
   }

   public static void setRequestStartTime(HttpServerExchange existing, HttpServerExchange newExchange) {
      newExchange.setRequestStartTime(existing.getRequestStartTime());
   }

   private static String addRfc6265ResponseCookieToExchange(Cookie cookie) {
      StringBuilder header = new StringBuilder(cookie.getName());
      header.append("=");
      if (cookie.getValue() != null) {
         header.append(cookie.getValue());
      }

      if (cookie.getPath() != null) {
         header.append("; Path=");
         header.append(cookie.getPath());
      }

      if (cookie.getDomain() != null) {
         header.append("; Domain=");
         header.append(cookie.getDomain());
      }

      if (cookie.isDiscard()) {
         header.append("; Discard");
      }

      if (cookie.isSecure()) {
         header.append("; Secure");
      }

      if (cookie.isHttpOnly()) {
         header.append("; HttpOnly");
      }

      if (cookie.getMaxAge() != null) {
         if (cookie.getMaxAge() >= 0) {
            header.append("; Max-Age=");
            header.append(cookie.getMaxAge());
         }

         if (cookie.getExpires() == null) {
            Date expires;
            if (cookie.getMaxAge() == 0) {
               expires = new Date();
               expires.setTime(0L);
               header.append("; Expires=");
               header.append(DateUtils.toOldCookieDateString(expires));
            } else if (cookie.getMaxAge() > 0) {
               expires = new Date();
               expires.setTime(expires.getTime() + (long)cookie.getMaxAge() * 1000L);
               header.append("; Expires=");
               header.append(DateUtils.toOldCookieDateString(expires));
            }
         }
      }

      if (cookie.getExpires() != null) {
         header.append("; Expires=");
         header.append(DateUtils.toDateString(cookie.getExpires()));
      }

      if (cookie.getComment() != null && !cookie.getComment().isEmpty()) {
         header.append("; Comment=");
         header.append(cookie.getComment());
      }

      if (cookie.isSameSite() && cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
         header.append("; SameSite=");
         header.append(cookie.getSameSiteMode());
      }

      return header.toString();
   }

   private static String addVersion0ResponseCookieToExchange(Cookie cookie) {
      StringBuilder header = new StringBuilder(cookie.getName());
      header.append("=");
      if (cookie.getValue() != null) {
         LegacyCookieSupport.maybeQuote(header, cookie.getValue());
      }

      if (cookie.getPath() != null) {
         header.append("; path=");
         LegacyCookieSupport.maybeQuote(header, cookie.getPath());
      }

      if (cookie.getDomain() != null) {
         header.append("; domain=");
         LegacyCookieSupport.maybeQuote(header, cookie.getDomain());
      }

      if (cookie.isSecure()) {
         header.append("; secure");
      }

      if (cookie.isHttpOnly()) {
         header.append("; HttpOnly");
      }

      if (cookie.getExpires() != null) {
         header.append("; Expires=");
         header.append(DateUtils.toOldCookieDateString(cookie.getExpires()));
      } else if (cookie.getMaxAge() != null) {
         if (cookie.getMaxAge() >= 0) {
            header.append("; Max-Age=");
            header.append(cookie.getMaxAge());
         }

         Date expires;
         if (cookie.getMaxAge() == 0) {
            expires = new Date();
            expires.setTime(0L);
            header.append("; Expires=");
            header.append(DateUtils.toOldCookieDateString(expires));
         } else if (cookie.getMaxAge() > 0) {
            expires = new Date();
            expires.setTime(expires.getTime() + (long)cookie.getMaxAge() * 1000L);
            header.append("; Expires=");
            header.append(DateUtils.toOldCookieDateString(expires));
         }
      }

      if (cookie.isSameSite() && cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
         header.append("; SameSite=");
         header.append(cookie.getSameSiteMode());
      }

      return header.toString();
   }

   private static String addVersion1ResponseCookieToExchange(Cookie cookie) {
      StringBuilder header = new StringBuilder(cookie.getName());
      header.append("=");
      if (cookie.getValue() != null) {
         LegacyCookieSupport.maybeQuote(header, cookie.getValue());
      }

      header.append("; Version=1");
      if (cookie.getPath() != null) {
         header.append("; Path=");
         LegacyCookieSupport.maybeQuote(header, cookie.getPath());
      }

      if (cookie.getDomain() != null) {
         header.append("; Domain=");
         LegacyCookieSupport.maybeQuote(header, cookie.getDomain());
      }

      if (cookie.isDiscard()) {
         header.append("; Discard");
      }

      if (cookie.isSecure()) {
         header.append("; Secure");
      }

      if (cookie.isHttpOnly()) {
         header.append("; HttpOnly");
      }

      if (cookie.getMaxAge() != null) {
         if (cookie.getMaxAge() >= 0) {
            header.append("; Max-Age=");
            header.append(cookie.getMaxAge());
         }

         if (cookie.getExpires() == null) {
            Date expires;
            if (cookie.getMaxAge() == 0) {
               expires = new Date();
               expires.setTime(0L);
               header.append("; Expires=");
               header.append(DateUtils.toOldCookieDateString(expires));
            } else if (cookie.getMaxAge() > 0) {
               expires = new Date();
               expires.setTime(expires.getTime() + (long)cookie.getMaxAge() * 1000L);
               header.append("; Expires=");
               header.append(DateUtils.toOldCookieDateString(expires));
            }
         }
      }

      if (cookie.getExpires() != null) {
         header.append("; Expires=");
         header.append(DateUtils.toDateString(cookie.getExpires()));
      }

      if (cookie.getComment() != null && !cookie.getComment().isEmpty()) {
         header.append("; Comment=");
         LegacyCookieSupport.maybeQuote(header, cookie.getComment());
      }

      if (cookie.isSameSite() && cookie.getSameSiteMode() != null && !cookie.getSameSiteMode().isEmpty()) {
         header.append("; SameSite=");
         header.append(cookie.getSameSiteMode());
      }

      return header.toString();
   }

   public static void executeRootHandler(HttpHandler handler, HttpServerExchange exchange) {
      try {
         exchange.setInCall(true);
         handler.handleRequest(exchange);
         exchange.setInCall(false);
         boolean resumed = exchange.isResumed();
         if (exchange.isDispatched()) {
            if (resumed) {
               UndertowLogger.REQUEST_LOGGER.resumedAndDispatched();
               exchange.setStatusCode(500);
               exchange.endExchange();
               return;
            }

            Runnable dispatchTask = exchange.getDispatchTask();
            Executor executor = exchange.getDispatchExecutor();
            exchange.setDispatchExecutor((Executor)null);
            exchange.unDispatch();
            if (dispatchTask != null) {
               Executor executor = executor == null ? exchange.getConnection().getWorker() : executor;

               try {
                  ((Executor)executor).execute(dispatchTask);
               } catch (RejectedExecutionException var6) {
                  UndertowLogger.REQUEST_LOGGER.debug("Failed to dispatch to worker", var6);
                  exchange.setStatusCode(503);
                  exchange.endExchange();
               }
            }
         } else if (!resumed) {
            exchange.endExchange();
         } else {
            exchange.runResumeReadWrite();
         }
      } catch (Throwable var7) {
         exchange.putAttachment(DefaultResponseListener.EXCEPTION, var7);
         exchange.setInCall(false);
         if (!exchange.isResponseStarted()) {
            exchange.setStatusCode(500);
         }

         if (var7 instanceof IOException) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var7);
         } else {
            UndertowLogger.REQUEST_LOGGER.undertowRequestFailed(var7, exchange);
         }

         exchange.endExchange();
      }

   }

   /** @deprecated */
   @Deprecated
   public static void setExchangeRequestPath(HttpServerExchange exchange, String encodedPath, String charset, boolean decode, boolean allowEncodedSlash, StringBuilder decodeBuffer) {
      try {
         setExchangeRequestPath(exchange, encodedPath, charset, decode, allowEncodedSlash, decodeBuffer, exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
      } catch (ParameterLimitException var7) {
         throw new RuntimeException(var7);
      }
   }

   public static void setExchangeRequestPath(HttpServerExchange exchange, String encodedPath, String charset, boolean decode, boolean allowEncodedSlash, StringBuilder decodeBuffer, int maxParameters) throws ParameterLimitException {
      boolean requiresDecode = false;
      StringBuilder pathBuilder = new StringBuilder();
      int currentPathPartIndex = 0;

      for(int i = 0; i < encodedPath.length(); ++i) {
         char c = encodedPath.charAt(i);
         String part;
         String encodedPart;
         if (c == '?') {
            encodedPart = encodedPath.substring(currentPathPartIndex, i);
            if (requiresDecode) {
               part = URLUtils.decode(encodedPart, charset, allowEncodedSlash, false, decodeBuffer);
            } else {
               part = encodedPart;
            }

            pathBuilder.append(part);
            part = pathBuilder.toString();
            exchange.setRequestPath(part);
            exchange.setRelativePath(part);
            exchange.setRequestURI(encodedPath.substring(0, i));
            String qs = encodedPath.substring(i + 1);
            exchange.setQueryString(qs);
            URLUtils.parseQueryString(qs, exchange, charset, decode, maxParameters);
            return;
         }

         if (c == ';') {
            encodedPart = encodedPath.substring(currentPathPartIndex, i);
            if (requiresDecode) {
               part = URLUtils.decode(encodedPart, charset, allowEncodedSlash, false, decodeBuffer);
            } else {
               part = encodedPart;
            }

            pathBuilder.append(part);
            exchange.setRequestURI(encodedPath);
            currentPathPartIndex = i + 1 + URLUtils.parsePathParams(encodedPath.substring(i + 1), exchange, charset, decode, maxParameters);
            i = currentPathPartIndex - 1;
         } else if (c == '%' || c == '+') {
            requiresDecode = decode;
         }
      }

      String encodedPart = encodedPath.substring(currentPathPartIndex);
      String part;
      if (requiresDecode) {
         part = URLUtils.decode(encodedPart, charset, allowEncodedSlash, false, decodeBuffer);
      } else {
         part = encodedPart;
      }

      pathBuilder.append(part);
      part = pathBuilder.toString();
      exchange.setRequestPath(part);
      exchange.setRelativePath(part);
      exchange.setRequestURI(encodedPath);
   }

   public static StreamSourceChannel getExistingRequestChannel(HttpServerExchange exchange) {
      return exchange.requestChannel;
   }

   public static boolean isEntityBodyAllowed(HttpServerExchange exchange) {
      int code = exchange.getStatusCode();
      return isEntityBodyAllowed(code);
   }

   public static boolean isEntityBodyAllowed(int code) {
      if (code >= 100 && code < 200) {
         return false;
      } else {
         return code != 204 && code != 304;
      }
   }

   public static void updateResponseBytesSent(HttpServerExchange exchange, long bytes) {
      exchange.updateBytesSent(bytes);
   }

   public static ConduitStreamSinkChannel getConduitSinkChannel(HttpServerExchange exchange) {
      return exchange.getConnection().getSinkChannel();
   }

   public static void verifyToken(HttpString header) {
      int length = header.length();

      for(int i = 0; i < length; ++i) {
         byte c = header.byteAt(i);
         if (!ALLOWED_TOKEN_CHARACTERS[c]) {
            throw UndertowMessages.MESSAGES.invalidToken(c);
         }
      }

   }

   public static boolean isValidTokenCharacter(byte c) {
      return ALLOWED_TOKEN_CHARACTERS[c];
   }

   public static boolean isValidSchemeCharacter(byte c) {
      return ALLOWED_SCHEME_CHARACTERS[c];
   }

   public static boolean areRequestHeadersValid(HeaderMap headers) {
      HeaderValues te = headers.get(Headers.TRANSFER_ENCODING);
      HeaderValues cl = headers.get(Headers.CONTENT_LENGTH);
      if (te != null && cl != null) {
         return false;
      } else if (te != null && te.size() > 1) {
         return false;
      } else {
         return cl == null || cl.size() <= 1;
      }
   }

   static {
      int i;
      for(i = 0; i < ALLOWED_TOKEN_CHARACTERS.length; ++i) {
         if (i >= 48 && i <= 57 || i >= 97 && i <= 122 || i >= 65 && i <= 90) {
            ALLOWED_TOKEN_CHARACTERS[i] = true;
         } else {
            switch (i) {
               case 33:
               case 35:
               case 36:
               case 37:
               case 38:
               case 39:
               case 42:
               case 43:
               case 45:
               case 46:
               case 94:
               case 95:
               case 96:
               case 124:
               case 126:
                  ALLOWED_TOKEN_CHARACTERS[i] = true;
                  break;
               default:
                  ALLOWED_TOKEN_CHARACTERS[i] = false;
            }
         }
      }

      for(i = 0; i < ALLOWED_SCHEME_CHARACTERS.length; ++i) {
         if (i >= 48 && i <= 57 || i >= 97 && i <= 122 || i >= 65 && i <= 90) {
            ALLOWED_SCHEME_CHARACTERS[i] = true;
         } else {
            switch (i) {
               case 43:
               case 45:
               case 46:
                  ALLOWED_SCHEME_CHARACTERS[i] = true;
                  break;
               case 44:
               default:
                  ALLOWED_SCHEME_CHARACTERS[i] = false;
            }
         }
      }

   }

   private static enum BufferedRequestDataCleanupListener implements ExchangeCompletionListener {
      INSTANCE;

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         PooledByteBuffer[] bufs = (PooledByteBuffer[])exchange.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (bufs != null) {
            PooledByteBuffer[] var4 = bufs;
            int var5 = bufs.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               PooledByteBuffer i = var4[var6];
               if (i != null) {
                  i.close();
               }
            }
         }

         nextListener.proceed();
      }
   }
}
