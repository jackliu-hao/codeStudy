package io.undertow.websockets.spi;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.websockets.core.WebSocketChannel;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.xnio.ChannelListener;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.channels.StreamSourceChannel;

public class AsyncWebSocketHttpServerExchange implements WebSocketHttpExchange {
   private final HttpServerExchange exchange;
   private Sender sender;
   private final Set<WebSocketChannel> peerConnections;

   public AsyncWebSocketHttpServerExchange(HttpServerExchange exchange, Set<WebSocketChannel> peerConnections) {
      this.exchange = exchange;
      this.peerConnections = peerConnections;
   }

   public <T> void putAttachment(AttachmentKey<T> key, T value) {
      this.exchange.putAttachment(key, value);
   }

   public <T> T getAttachment(AttachmentKey<T> key) {
      return this.exchange.getAttachment(key);
   }

   public String getRequestHeader(String headerName) {
      return this.exchange.getRequestHeaders().getFirst(HttpString.tryFromString(headerName));
   }

   public Map<String, List<String>> getRequestHeaders() {
      Map<String, List<String>> headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      Iterator var2 = this.exchange.getRequestHeaders().getHeaderNames().iterator();

      while(var2.hasNext()) {
         HttpString header = (HttpString)var2.next();
         headers.put(header.toString(), new ArrayList(this.exchange.getRequestHeaders().get(header)));
      }

      return Collections.unmodifiableMap(headers);
   }

   public String getResponseHeader(String headerName) {
      return this.exchange.getResponseHeaders().getFirst(HttpString.tryFromString(headerName));
   }

   public Map<String, List<String>> getResponseHeaders() {
      Map<String, List<String>> headers = new HashMap();
      Iterator var2 = this.exchange.getResponseHeaders().getHeaderNames().iterator();

      while(var2.hasNext()) {
         HttpString header = (HttpString)var2.next();
         headers.put(header.toString(), new ArrayList(this.exchange.getResponseHeaders().get(header)));
      }

      return Collections.unmodifiableMap(headers);
   }

   public void setResponseHeaders(Map<String, List<String>> headers) {
      HeaderMap map = this.exchange.getRequestHeaders();
      map.clear();
      Iterator var3 = headers.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, List<String>> header = (Map.Entry)var3.next();
         map.addAll(HttpString.tryFromString((String)header.getKey()), (Collection)header.getValue());
      }

   }

   public void setResponseHeader(String headerName, String headerValue) {
      this.exchange.getResponseHeaders().put(HttpString.tryFromString(headerName), headerValue);
   }

   public void upgradeChannel(HttpUpgradeListener upgradeCallback) {
      this.exchange.upgradeChannel(upgradeCallback);
   }

   public IoFuture<Void> sendData(ByteBuffer data) {
      if (this.sender == null) {
         this.sender = this.exchange.getResponseSender();
      }

      final FutureResult<Void> future = new FutureResult();
      this.sender.send(data, new IoCallback() {
         public void onComplete(HttpServerExchange exchange, Sender sender) {
            future.setResult((Object)null);
         }

         public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
            future.setException(exception);
         }
      });
      return future.getIoFuture();
   }

   public IoFuture<byte[]> readRequestData() {
      final ByteArrayOutputStream data = new ByteArrayOutputStream();
      PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
      final ByteBuffer buffer = pooled.getBuffer();
      StreamSourceChannel channel = this.exchange.getRequestChannel();

      while(true) {
         try {
            int res = channel.read(buffer);
            if (res == -1) {
               return new FinishedIoFuture(data.toByteArray());
            }

            if (res == 0) {
               final FutureResult<byte[]> future = new FutureResult();
               channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                  public void handleEvent(StreamSourceChannel channel) {
                     try {
                        int res = channel.read(buffer);
                        if (res == -1) {
                           future.setResult(data.toByteArray());
                           channel.suspendReads();
                           return;
                        }

                        if (res == 0) {
                           return;
                        }

                        buffer.flip();

                        while(buffer.hasRemaining()) {
                           data.write(buffer.get());
                        }

                        buffer.clear();
                     } catch (IOException var4) {
                        future.setException(var4);
                     }

                  }
               });
               channel.resumeReads();
               return future.getIoFuture();
            }

            buffer.flip();

            while(buffer.hasRemaining()) {
               data.write(buffer.get());
            }

            buffer.clear();
         } catch (IOException var8) {
            FutureResult<byte[]> future = new FutureResult();
            future.setException(var8);
            return future.getIoFuture();
         }
      }
   }

   public void endExchange() {
      this.exchange.endExchange();
   }

   public void close() {
      try {
         this.exchange.endExchange();
      } finally {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
      }

   }

   public String getRequestScheme() {
      return this.exchange.getRequestScheme();
   }

   public String getRequestURI() {
      String q = this.exchange.getQueryString();
      return q != null && !q.isEmpty() ? this.exchange.getRequestURI() + "?" + q : this.exchange.getRequestURI();
   }

   public ByteBufferPool getBufferPool() {
      return this.exchange.getConnection().getByteBufferPool();
   }

   public String getQueryString() {
      return this.exchange.getQueryString();
   }

   public Object getSession() {
      SessionManager sm = (SessionManager)this.exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
      SessionConfig sessionCookieConfig = (SessionConfig)this.exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
      return sm != null && sessionCookieConfig != null ? sm.getSession(this.exchange, sessionCookieConfig) : null;
   }

   public Map<String, List<String>> getRequestParameters() {
      Map<String, List<String>> params = new HashMap();
      Iterator var2 = this.exchange.getQueryParameters().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Deque<String>> param = (Map.Entry)var2.next();
         params.put(param.getKey(), new ArrayList((Collection)param.getValue()));
      }

      return params;
   }

   public Principal getUserPrincipal() {
      SecurityContext sc = this.exchange.getSecurityContext();
      if (sc == null) {
         return null;
      } else {
         Account authenticatedAccount = sc.getAuthenticatedAccount();
         return authenticatedAccount == null ? null : authenticatedAccount.getPrincipal();
      }
   }

   public boolean isUserInRole(String role) {
      SecurityContext sc = this.exchange.getSecurityContext();
      if (sc == null) {
         return false;
      } else {
         Account authenticatedAccount = sc.getAuthenticatedAccount();
         return authenticatedAccount == null ? false : authenticatedAccount.getRoles().contains(role);
      }
   }

   public Set<WebSocketChannel> getPeerConnections() {
      return this.peerConnections;
   }

   public OptionMap getOptions() {
      return this.exchange.getConnection().getUndertowOptions();
   }
}
