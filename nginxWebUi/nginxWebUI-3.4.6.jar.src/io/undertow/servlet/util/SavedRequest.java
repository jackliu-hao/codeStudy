/*     */ package io.undertow.servlet.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpSession;
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
/*     */ public class SavedRequest
/*     */   implements Serializable
/*     */ {
/*  53 */   private static final String SESSION_KEY = SavedRequest.class.getName();
/*     */   
/*     */   private final byte[] data;
/*     */   private final int dataLength;
/*     */   private final HttpString method;
/*     */   private final String requestPath;
/*  59 */   private final HashMap<HttpString, List<String>> headerMap = new HashMap<>();
/*     */   
/*     */   public SavedRequest(byte[] data, int dataLength, HttpString method, String requestPath, HeaderMap headerMap) {
/*  62 */     this.data = data;
/*  63 */     this.dataLength = dataLength;
/*  64 */     this.method = method;
/*  65 */     this.requestPath = requestPath;
/*  66 */     for (HeaderValues val : headerMap) {
/*  67 */       this.headerMap.put(val.getHeaderName(), new ArrayList<>((Collection<? extends String>)val));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getMaxBufferSizeToSave(HttpServerExchange exchange) {
/*  78 */     int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
/*  79 */     return maxSize;
/*     */   }
/*     */   
/*     */   public static void trySaveRequest(HttpServerExchange exchange) {
/*  83 */     int maxSize = getMaxBufferSizeToSave(exchange);
/*  84 */     if (maxSize > 0)
/*     */     {
/*  86 */       if (!exchange.isRequestComplete()) {
/*  87 */         long requestContentLength = exchange.getRequestContentLength();
/*  88 */         if (requestContentLength > maxSize) {
/*  89 */           UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/*  94 */         byte[] buffer = new byte[maxSize];
/*  95 */         int read = 0;
/*  96 */         int res = 0;
/*  97 */         InputStream in = exchange.getInputStream();
/*     */         try {
/*  99 */           while ((res = in.read(buffer, read, buffer.length - read)) > 0) {
/* 100 */             read += res;
/* 101 */             if (read == maxSize) {
/* 102 */               UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
/*     */               
/*     */               return;
/*     */             } 
/*     */           } 
/* 107 */           trySaveRequest(exchange, buffer, read);
/* 108 */         } catch (IOException e) {
/* 109 */           UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static void trySaveRequest(HttpServerExchange exchange, byte[] buffer, int length) {
/* 116 */     int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
/* 117 */     if (maxSize > 0) {
/* 118 */       Session underlyingSession; if (length > maxSize) {
/* 119 */         UndertowLogger.REQUEST_LOGGER.debugf("Request to %s was to large to save", exchange.getRequestURI());
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 124 */       HeaderMap headers = new HeaderMap();
/* 125 */       for (HeaderValues entry : exchange.getRequestHeaders()) {
/* 126 */         if (entry.getHeaderName().equals(Headers.CONTENT_LENGTH) || entry
/* 127 */           .getHeaderName().equals(Headers.TRANSFER_ENCODING) || entry
/* 128 */           .getHeaderName().equals(Headers.CONNECTION)) {
/*     */           continue;
/*     */         }
/* 131 */         headers.putAll(entry.getHeaderName(), (Collection)entry);
/*     */       } 
/* 133 */       SavedRequest request = new SavedRequest(buffer, length, exchange.getRequestMethod(), exchange.getRelativePath(), exchange.getRequestHeaders());
/* 134 */       ServletRequestContext sc = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 135 */       HttpSessionImpl session = sc.getCurrentServletContext().getSession(exchange, true);
/*     */       
/* 137 */       if (System.getSecurityManager() == null) {
/* 138 */         underlyingSession = session.getSession();
/*     */       } else {
/* 140 */         underlyingSession = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)session));
/*     */       } 
/* 142 */       underlyingSession.setAttribute(SESSION_KEY, request);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void tryRestoreRequest(HttpServerExchange exchange, HttpSession session) {
/* 147 */     if (session instanceof HttpSessionImpl) {
/*     */       Session underlyingSession;
/*     */       
/* 150 */       if (System.getSecurityManager() == null) {
/* 151 */         underlyingSession = ((HttpSessionImpl)session).getSession();
/*     */       } else {
/* 153 */         underlyingSession = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction(session));
/*     */       } 
/* 155 */       SavedRequest request = (SavedRequest)underlyingSession.getAttribute(SESSION_KEY);
/* 156 */       if (request != null && 
/* 157 */         request.requestPath.equals(exchange.getRelativePath()) && exchange.isRequestComplete()) {
/* 158 */         UndertowLogger.REQUEST_LOGGER.debugf("restoring request body for request to %s", request.requestPath);
/* 159 */         exchange.setRequestMethod(request.method);
/* 160 */         Connectors.ungetRequestBytes(exchange, new PooledByteBuffer[] { (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(request.data, 0, request.dataLength)) });
/* 161 */         underlyingSession.removeAttribute(SESSION_KEY);
/*     */ 
/*     */         
/* 164 */         Iterator<HeaderValues> headerIterator = exchange.getRequestHeaders().iterator();
/* 165 */         while (headerIterator.hasNext()) {
/* 166 */           HeaderValues header = headerIterator.next();
/* 167 */           if (!header.getHeaderName().equals(Headers.CONNECTION)) {
/* 168 */             headerIterator.remove();
/*     */           }
/*     */         } 
/* 171 */         for (Map.Entry<HttpString, List<String>> header : request.headerMap.entrySet())
/* 172 */           exchange.getRequestHeaders().putAll(header.getKey(), header.getValue()); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\SavedRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */