package io.undertow.servlet.websockets;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.util.AttachmentKey;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

public class ServletWebSocketHttpExchange implements WebSocketHttpExchange {
   private final HttpServletRequest request;
   private final HttpServletResponse response;
   private final HttpServerExchange exchange;
   private final Set<WebSocketChannel> peerConnections;

   public ServletWebSocketHttpExchange(HttpServletRequest request, HttpServletResponse response, Set<WebSocketChannel> peerConnections) {
      this.request = request;
      this.response = response;
      this.peerConnections = peerConnections;
      this.exchange = SecurityActions.requireCurrentServletRequestContext().getOriginalRequest().getExchange();
   }

   public <T> void putAttachment(AttachmentKey<T> key, T value) {
      this.exchange.putAttachment(key, value);
   }

   public <T> T getAttachment(AttachmentKey<T> key) {
      return this.exchange.getAttachment(key);
   }

   public String getRequestHeader(String headerName) {
      return this.request.getHeader(headerName);
   }

   public Map<String, List<String>> getRequestHeaders() {
      Map<String, List<String>> headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      Enumeration<String> headerNames = this.request.getHeaderNames();

      while(headerNames.hasMoreElements()) {
         String header = (String)headerNames.nextElement();
         Enumeration<String> theHeaders = this.request.getHeaders(header);
         List<String> vals = new ArrayList();
         headers.put(header, vals);

         while(theHeaders.hasMoreElements()) {
            vals.add(theHeaders.nextElement());
         }
      }

      return Collections.unmodifiableMap(headers);
   }

   public String getResponseHeader(String headerName) {
      return this.response.getHeader(headerName);
   }

   public Map<String, List<String>> getResponseHeaders() {
      Map<String, List<String>> headers = new HashMap();
      Collection<String> headerNames = this.response.getHeaderNames();
      Iterator var3 = headerNames.iterator();

      while(var3.hasNext()) {
         String header = (String)var3.next();
         headers.put(header, new ArrayList(this.response.getHeaders(header)));
      }

      return Collections.unmodifiableMap(headers);
   }

   public void setResponseHeaders(Map<String, List<String>> headers) {
      Iterator var2 = this.response.getHeaderNames().iterator();

      while(var2.hasNext()) {
         String header = (String)var2.next();
         this.response.setHeader(header, (String)null);
      }

      var2 = headers.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, List<String>> entry = (Map.Entry)var2.next();
         Iterator var4 = ((List)entry.getValue()).iterator();

         while(var4.hasNext()) {
            String val = (String)var4.next();
            this.response.addHeader((String)entry.getKey(), val);
         }
      }

   }

   public void setResponseHeader(String headerName, String headerValue) {
      this.response.setHeader(headerName, headerValue);
   }

   public void upgradeChannel(HttpUpgradeListener upgradeCallback) {
      this.exchange.upgradeChannel(upgradeCallback);
   }

   public IoFuture<Void> sendData(ByteBuffer data) {
      try {
         ServletOutputStream outputStream = this.response.getOutputStream();

         while(data.hasRemaining()) {
            outputStream.write(data.get());
         }

         return new FinishedIoFuture((Object)null);
      } catch (IOException var4) {
         FutureResult<Void> ioFuture = new FutureResult();
         ioFuture.setException(var4);
         return ioFuture.getIoFuture();
      }
   }

   public IoFuture<byte[]> readRequestData() {
      ByteArrayOutputStream data = new ByteArrayOutputStream();

      try {
         ServletInputStream in = this.request.getInputStream();
         byte[] buf = new byte[1024];

         int r;
         while((r = in.read(buf)) != -1) {
            data.write(buf, 0, r);
         }

         return new FinishedIoFuture(data.toByteArray());
      } catch (IOException var5) {
         FutureResult<byte[]> ioFuture = new FutureResult();
         ioFuture.setException(var5);
         return ioFuture.getIoFuture();
      }
   }

   public void endExchange() {
   }

   public void close() {
      IoUtils.safeClose((Closeable)this.exchange.getConnection());
   }

   public String getRequestScheme() {
      return this.request.getScheme();
   }

   public String getRequestURI() {
      return this.request.getRequestURI() + (this.request.getQueryString() == null ? "" : "?" + this.request.getQueryString());
   }

   public ByteBufferPool getBufferPool() {
      return this.exchange.getConnection().getByteBufferPool();
   }

   public String getQueryString() {
      return this.request.getQueryString();
   }

   public Object getSession() {
      return this.request.getSession(false);
   }

   public Map<String, List<String>> getRequestParameters() {
      Map<String, List<String>> params = new HashMap();
      Iterator var2 = this.request.getParameterMap().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, String[]> param = (Map.Entry)var2.next();
         params.put(param.getKey(), new ArrayList(Arrays.asList((Object[])param.getValue())));
      }

      return params;
   }

   public Principal getUserPrincipal() {
      return this.request.getUserPrincipal();
   }

   public boolean isUserInRole(String role) {
      return this.request.isUserInRole(role);
   }

   public Set<WebSocketChannel> getPeerConnections() {
      return this.peerConnections;
   }

   public OptionMap getOptions() {
      return this.exchange.getConnection().getUndertowOptions();
   }
}
