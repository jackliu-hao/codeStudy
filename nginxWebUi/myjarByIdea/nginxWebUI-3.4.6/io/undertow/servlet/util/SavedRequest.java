package io.undertow.servlet.util;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

public class SavedRequest implements Serializable {
   private static final String SESSION_KEY = SavedRequest.class.getName();
   private final byte[] data;
   private final int dataLength;
   private final HttpString method;
   private final String requestPath;
   private final HashMap<HttpString, List<String>> headerMap = new HashMap();

   public SavedRequest(byte[] data, int dataLength, HttpString method, String requestPath, HeaderMap headerMap) {
      this.data = data;
      this.dataLength = dataLength;
      this.method = method;
      this.requestPath = requestPath;
      Iterator var6 = headerMap.iterator();

      while(var6.hasNext()) {
         HeaderValues val = (HeaderValues)var6.next();
         this.headerMap.put(val.getHeaderName(), new ArrayList(val));
      }

   }

   public static int getMaxBufferSizeToSave(HttpServerExchange exchange) {
      int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
      return maxSize;
   }

   public static void trySaveRequest(HttpServerExchange exchange) {
      int maxSize = getMaxBufferSizeToSave(exchange);
      if (maxSize > 0 && !exchange.isRequestComplete()) {
         long requestContentLength = exchange.getRequestContentLength();
         if (requestContentLength > (long)maxSize) {
            UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
            return;
         }

         byte[] buffer = new byte[maxSize];
         int read = 0;
         int res = false;
         InputStream in = exchange.getInputStream();

         try {
            int res;
            while((res = in.read(buffer, read, buffer.length - read)) > 0) {
               read += res;
               if (read == maxSize) {
                  UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
                  return;
               }
            }

            trySaveRequest(exchange, buffer, read);
         } catch (IOException var9) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(var9);
         }
      }

   }

   public static void trySaveRequest(HttpServerExchange exchange, byte[] buffer, int length) {
      int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
      if (maxSize > 0) {
         if (length > maxSize) {
            UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
            return;
         }

         HeaderMap headers = new HeaderMap();
         Iterator var5 = exchange.getRequestHeaders().iterator();

         while(var5.hasNext()) {
            HeaderValues entry = (HeaderValues)var5.next();
            if (!entry.getHeaderName().equals(Headers.CONTENT_LENGTH) && !entry.getHeaderName().equals(Headers.TRANSFER_ENCODING) && !entry.getHeaderName().equals(Headers.CONNECTION)) {
               headers.putAll(entry.getHeaderName(), entry);
            }
         }

         SavedRequest request = new SavedRequest(buffer, length, exchange.getRequestMethod(), exchange.getRelativePath(), exchange.getRequestHeaders());
         ServletRequestContext sc = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         HttpSessionImpl session = sc.getCurrentServletContext().getSession(exchange, true);
         Session underlyingSession;
         if (System.getSecurityManager() == null) {
            underlyingSession = session.getSession();
         } else {
            underlyingSession = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(session));
         }

         underlyingSession.setAttribute(SESSION_KEY, request);
      }

   }

   public static void tryRestoreRequest(HttpServerExchange exchange, HttpSession session) {
      if (session instanceof HttpSessionImpl) {
         Session underlyingSession;
         if (System.getSecurityManager() == null) {
            underlyingSession = ((HttpSessionImpl)session).getSession();
         } else {
            underlyingSession = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(session));
         }

         SavedRequest request = (SavedRequest)underlyingSession.getAttribute(SESSION_KEY);
         if (request != null && request.requestPath.equals(exchange.getRelativePath()) && exchange.isRequestComplete()) {
            UndertowLogger.REQUEST_LOGGER.debugf("restoring request body for request to %s", request.requestPath);
            exchange.setRequestMethod(request.method);
            Connectors.ungetRequestBytes(exchange, new ImmediatePooledByteBuffer(ByteBuffer.wrap(request.data, 0, request.dataLength)));
            underlyingSession.removeAttribute(SESSION_KEY);
            Iterator<HeaderValues> headerIterator = exchange.getRequestHeaders().iterator();

            while(headerIterator.hasNext()) {
               HeaderValues header = (HeaderValues)headerIterator.next();
               if (!header.getHeaderName().equals(Headers.CONNECTION)) {
                  headerIterator.remove();
               }
            }

            Iterator var7 = request.headerMap.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry<HttpString, List<String>> header = (Map.Entry)var7.next();
               exchange.getRequestHeaders().putAll((HttpString)header.getKey(), (Collection)header.getValue());
            }
         }
      }

   }
}
