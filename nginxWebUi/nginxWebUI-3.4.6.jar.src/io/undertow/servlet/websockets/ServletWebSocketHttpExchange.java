/*     */ package io.undertow.servlet.websockets;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.Principal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.xnio.FinishedIoFuture;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletWebSocketHttpExchange
/*     */   implements WebSocketHttpExchange
/*     */ {
/*     */   private final HttpServletRequest request;
/*     */   private final HttpServletResponse response;
/*     */   private final HttpServerExchange exchange;
/*     */   private final Set<WebSocketChannel> peerConnections;
/*     */   
/*     */   public ServletWebSocketHttpExchange(HttpServletRequest request, HttpServletResponse response, Set<WebSocketChannel> peerConnections) {
/*  63 */     this.request = request;
/*  64 */     this.response = response;
/*  65 */     this.peerConnections = peerConnections;
/*  66 */     this.exchange = SecurityActions.requireCurrentServletRequestContext().getOriginalRequest().getExchange();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void putAttachment(AttachmentKey<T> key, T value) {
/*  72 */     this.exchange.putAttachment(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getAttachment(AttachmentKey<T> key) {
/*  77 */     return (T)this.exchange.getAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestHeader(String headerName) {
/*  82 */     return this.request.getHeader(headerName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getRequestHeaders() {
/*  87 */     Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  88 */     Enumeration<String> headerNames = this.request.getHeaderNames();
/*  89 */     while (headerNames.hasMoreElements()) {
/*  90 */       String header = headerNames.nextElement();
/*  91 */       Enumeration<String> theHeaders = this.request.getHeaders(header);
/*  92 */       List<String> vals = new ArrayList<>();
/*  93 */       headers.put(header, vals);
/*  94 */       while (theHeaders.hasMoreElements()) {
/*  95 */         vals.add(theHeaders.nextElement());
/*     */       }
/*     */     } 
/*     */     
/*  99 */     return Collections.unmodifiableMap(headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getResponseHeader(String headerName) {
/* 104 */     return this.response.getHeader(headerName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getResponseHeaders() {
/* 109 */     Map<String, List<String>> headers = new HashMap<>();
/* 110 */     Collection<String> headerNames = this.response.getHeaderNames();
/* 111 */     for (String header : headerNames) {
/* 112 */       headers.put(header, new ArrayList<>(this.response.getHeaders(header)));
/*     */     }
/* 114 */     return Collections.unmodifiableMap(headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseHeaders(Map<String, List<String>> headers) {
/* 119 */     for (String header : this.response.getHeaderNames()) {
/* 120 */       this.response.setHeader(header, null);
/*     */     }
/*     */     
/* 123 */     for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
/* 124 */       for (String val : entry.getValue()) {
/* 125 */         this.response.addHeader(entry.getKey(), val);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseHeader(String headerName, String headerValue) {
/* 132 */     this.response.setHeader(headerName, headerValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void upgradeChannel(HttpUpgradeListener upgradeCallback) {
/* 137 */     this.exchange.upgradeChannel(upgradeCallback);
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<Void> sendData(ByteBuffer data) {
/*     */     try {
/* 143 */       ServletOutputStream outputStream = this.response.getOutputStream();
/* 144 */       while (data.hasRemaining()) {
/* 145 */         outputStream.write(data.get());
/*     */       }
/* 147 */       return (IoFuture<Void>)new FinishedIoFuture(null);
/* 148 */     } catch (IOException e) {
/* 149 */       FutureResult<Void> ioFuture = new FutureResult();
/* 150 */       ioFuture.setException(e);
/* 151 */       return ioFuture.getIoFuture();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<byte[]> readRequestData() {
/* 157 */     ByteArrayOutputStream data = new ByteArrayOutputStream();
/*     */     try {
/* 159 */       ServletInputStream in = this.request.getInputStream();
/* 160 */       byte[] buf = new byte[1024];
/*     */       int r;
/* 162 */       while ((r = in.read(buf)) != -1) {
/* 163 */         data.write(buf, 0, r);
/*     */       }
/* 165 */       return (IoFuture<byte[]>)new FinishedIoFuture(data.toByteArray());
/* 166 */     } catch (IOException e) {
/* 167 */       FutureResult<byte[]> ioFuture = new FutureResult();
/* 168 */       ioFuture.setException(e);
/* 169 */       return ioFuture.getIoFuture();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endExchange() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 181 */     IoUtils.safeClose((Closeable)this.exchange.getConnection());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestScheme() {
/* 186 */     return this.request.getScheme();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestURI() {
/* 191 */     return this.request.getRequestURI() + ((this.request.getQueryString() == null) ? "" : ("?" + this.request.getQueryString()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 196 */     return this.exchange.getConnection().getByteBufferPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getQueryString() {
/* 201 */     return this.request.getQueryString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getSession() {
/* 206 */     return this.request.getSession(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getRequestParameters() {
/* 211 */     Map<String, List<String>> params = new HashMap<>();
/* 212 */     for (Map.Entry<String, String[]> param : (Iterable<Map.Entry<String, String[]>>)this.request.getParameterMap().entrySet()) {
/* 213 */       params.put(param.getKey(), new ArrayList<>(Arrays.asList((Object[])param.getValue())));
/*     */     }
/* 215 */     return params;
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getUserPrincipal() {
/* 220 */     return this.request.getUserPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role) {
/* 225 */     return this.request.isUserInRole(role);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<WebSocketChannel> getPeerConnections() {
/* 230 */     return this.peerConnections;
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getOptions() {
/* 235 */     return this.exchange.getConnection().getUndertowOptions();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\websockets\ServletWebSocketHttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */