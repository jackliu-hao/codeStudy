/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.http.ConnectionClosedException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpConnectionMetrics;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpInetConnection;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.entity.BasicHttpEntity;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.LaxContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.StrictContentLengthStrategy;
/*     */ import org.apache.http.impl.io.ChunkedInputStream;
/*     */ import org.apache.http.impl.io.ChunkedOutputStream;
/*     */ import org.apache.http.impl.io.ContentLengthInputStream;
/*     */ import org.apache.http.impl.io.ContentLengthOutputStream;
/*     */ import org.apache.http.impl.io.EmptyInputStream;
/*     */ import org.apache.http.impl.io.HttpTransportMetricsImpl;
/*     */ import org.apache.http.impl.io.IdentityInputStream;
/*     */ import org.apache.http.impl.io.IdentityOutputStream;
/*     */ import org.apache.http.impl.io.SessionInputBufferImpl;
/*     */ import org.apache.http.impl.io.SessionOutputBufferImpl;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.NetUtils;
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
/*     */ public class BHttpConnectionBase
/*     */   implements HttpInetConnection
/*     */ {
/*     */   private final SessionInputBufferImpl inBuffer;
/*     */   private final SessionOutputBufferImpl outbuffer;
/*     */   private final MessageConstraints messageConstraints;
/*     */   private final HttpConnectionMetricsImpl connMetrics;
/*     */   private final ContentLengthStrategy incomingContentStrategy;
/*     */   private final ContentLengthStrategy outgoingContentStrategy;
/*     */   private final AtomicReference<Socket> socketHolder;
/*     */   
/*     */   protected BHttpConnectionBase(int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints messageConstraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
/* 111 */     Args.positive(bufferSize, "Buffer size");
/* 112 */     HttpTransportMetricsImpl inTransportMetrics = new HttpTransportMetricsImpl();
/* 113 */     HttpTransportMetricsImpl outTransportMetrics = new HttpTransportMetricsImpl();
/* 114 */     this.inBuffer = new SessionInputBufferImpl(inTransportMetrics, bufferSize, -1, (messageConstraints != null) ? messageConstraints : MessageConstraints.DEFAULT, charDecoder);
/*     */     
/* 116 */     this.outbuffer = new SessionOutputBufferImpl(outTransportMetrics, bufferSize, fragmentSizeHint, charEncoder);
/*     */     
/* 118 */     this.messageConstraints = messageConstraints;
/* 119 */     this.connMetrics = new HttpConnectionMetricsImpl((HttpTransportMetrics)inTransportMetrics, (HttpTransportMetrics)outTransportMetrics);
/* 120 */     this.incomingContentStrategy = (incomingContentStrategy != null) ? incomingContentStrategy : (ContentLengthStrategy)LaxContentLengthStrategy.INSTANCE;
/*     */     
/* 122 */     this.outgoingContentStrategy = (outgoingContentStrategy != null) ? outgoingContentStrategy : (ContentLengthStrategy)StrictContentLengthStrategy.INSTANCE;
/*     */     
/* 124 */     this.socketHolder = new AtomicReference<Socket>();
/*     */   }
/*     */   
/*     */   protected void ensureOpen() throws IOException {
/* 128 */     Socket socket = this.socketHolder.get();
/* 129 */     if (socket == null) {
/* 130 */       throw new ConnectionClosedException();
/*     */     }
/* 132 */     if (!this.inBuffer.isBound()) {
/* 133 */       this.inBuffer.bind(getSocketInputStream(socket));
/*     */     }
/* 135 */     if (!this.outbuffer.isBound()) {
/* 136 */       this.outbuffer.bind(getSocketOutputStream(socket));
/*     */     }
/*     */   }
/*     */   
/*     */   protected InputStream getSocketInputStream(Socket socket) throws IOException {
/* 141 */     return socket.getInputStream();
/*     */   }
/*     */   
/*     */   protected OutputStream getSocketOutputStream(Socket socket) throws IOException {
/* 145 */     return socket.getOutputStream();
/*     */   }
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
/*     */   protected void bind(Socket socket) throws IOException {
/* 159 */     Args.notNull(socket, "Socket");
/* 160 */     this.socketHolder.set(socket);
/* 161 */     this.inBuffer.bind(null);
/* 162 */     this.outbuffer.bind(null);
/*     */   }
/*     */   
/*     */   protected SessionInputBuffer getSessionInputBuffer() {
/* 166 */     return (SessionInputBuffer)this.inBuffer;
/*     */   }
/*     */   
/*     */   protected SessionOutputBuffer getSessionOutputBuffer() {
/* 170 */     return (SessionOutputBuffer)this.outbuffer;
/*     */   }
/*     */   
/*     */   protected void doFlush() throws IOException {
/* 174 */     this.outbuffer.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 179 */     return (this.socketHolder.get() != null);
/*     */   }
/*     */   
/*     */   protected Socket getSocket() {
/* 183 */     return this.socketHolder.get();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected OutputStream createOutputStream(long len, SessionOutputBuffer outbuffer) {
/* 189 */     if (len == -2L)
/* 190 */       return (OutputStream)new ChunkedOutputStream(2048, outbuffer); 
/* 191 */     if (len == -1L) {
/* 192 */       return (OutputStream)new IdentityOutputStream(outbuffer);
/*     */     }
/* 194 */     return (OutputStream)new ContentLengthOutputStream(outbuffer, len);
/*     */   }
/*     */ 
/*     */   
/*     */   protected OutputStream prepareOutput(HttpMessage message) throws HttpException {
/* 199 */     long len = this.outgoingContentStrategy.determineLength(message);
/* 200 */     return createOutputStream(len, (SessionOutputBuffer)this.outbuffer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected InputStream createInputStream(long len, SessionInputBuffer inBuffer) {
/* 206 */     if (len == -2L)
/* 207 */       return (InputStream)new ChunkedInputStream(inBuffer, this.messageConstraints); 
/* 208 */     if (len == -1L)
/* 209 */       return (InputStream)new IdentityInputStream(inBuffer); 
/* 210 */     if (len == 0L) {
/* 211 */       return (InputStream)EmptyInputStream.INSTANCE;
/*     */     }
/* 213 */     return (InputStream)new ContentLengthInputStream(inBuffer, len);
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpEntity prepareInput(HttpMessage message) throws HttpException {
/* 218 */     BasicHttpEntity entity = new BasicHttpEntity();
/*     */     
/* 220 */     long len = this.incomingContentStrategy.determineLength(message);
/* 221 */     InputStream inStream = createInputStream(len, (SessionInputBuffer)this.inBuffer);
/* 222 */     if (len == -2L) {
/* 223 */       entity.setChunked(true);
/* 224 */       entity.setContentLength(-1L);
/* 225 */       entity.setContent(inStream);
/* 226 */     } else if (len == -1L) {
/* 227 */       entity.setChunked(false);
/* 228 */       entity.setContentLength(-1L);
/* 229 */       entity.setContent(inStream);
/*     */     } else {
/* 231 */       entity.setChunked(false);
/* 232 */       entity.setContentLength(len);
/* 233 */       entity.setContent(inStream);
/*     */     } 
/*     */     
/* 236 */     Header contentTypeHeader = message.getFirstHeader("Content-Type");
/* 237 */     if (contentTypeHeader != null) {
/* 238 */       entity.setContentType(contentTypeHeader);
/*     */     }
/* 240 */     Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
/* 241 */     if (contentEncodingHeader != null) {
/* 242 */       entity.setContentEncoding(contentEncodingHeader);
/*     */     }
/* 244 */     return (HttpEntity)entity;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 249 */     Socket socket = this.socketHolder.get();
/* 250 */     return (socket != null) ? socket.getLocalAddress() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 255 */     Socket socket = this.socketHolder.get();
/* 256 */     return (socket != null) ? socket.getLocalPort() : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 261 */     Socket socket = this.socketHolder.get();
/* 262 */     return (socket != null) ? socket.getInetAddress() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 267 */     Socket socket = this.socketHolder.get();
/* 268 */     return (socket != null) ? socket.getPort() : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 273 */     Socket socket = this.socketHolder.get();
/* 274 */     if (socket != null) {
/*     */       try {
/* 276 */         socket.setSoTimeout(timeout);
/* 277 */       } catch (SocketException ignore) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 287 */     Socket socket = this.socketHolder.get();
/* 288 */     if (socket != null) {
/*     */       try {
/* 290 */         return socket.getSoTimeout();
/* 291 */       } catch (SocketException ignore) {
/* 292 */         return -1;
/*     */       } 
/*     */     }
/* 295 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 300 */     Socket socket = this.socketHolder.getAndSet(null);
/* 301 */     if (socket != null) {
/*     */ 
/*     */       
/* 304 */       try { socket.setSoLinger(true, 0); }
/* 305 */       catch (IOException ex) {  }
/*     */       finally
/* 307 */       { socket.close(); }
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 314 */     Socket socket = this.socketHolder.getAndSet(null);
/* 315 */     if (socket != null) {
/*     */       try {
/* 317 */         this.inBuffer.clear();
/* 318 */         this.outbuffer.flush();
/*     */         try {
/*     */           try {
/* 321 */             socket.shutdownOutput();
/* 322 */           } catch (IOException ignore) {}
/*     */           
/*     */           try {
/* 325 */             socket.shutdownInput();
/* 326 */           } catch (IOException ignore) {}
/*     */         }
/* 328 */         catch (UnsupportedOperationException ignore) {}
/*     */       }
/*     */       finally {
/*     */         
/* 332 */         socket.close();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private int fillInputBuffer(int timeout) throws IOException {
/* 338 */     Socket socket = this.socketHolder.get();
/* 339 */     int oldtimeout = socket.getSoTimeout();
/*     */     try {
/* 341 */       socket.setSoTimeout(timeout);
/* 342 */       return this.inBuffer.fillBuffer();
/*     */     } finally {
/* 344 */       socket.setSoTimeout(oldtimeout);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean awaitInput(int timeout) throws IOException {
/* 349 */     if (this.inBuffer.hasBufferedData()) {
/* 350 */       return true;
/*     */     }
/* 352 */     fillInputBuffer(timeout);
/* 353 */     return this.inBuffer.hasBufferedData();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 358 */     if (!isOpen()) {
/* 359 */       return true;
/*     */     }
/*     */     try {
/* 362 */       int bytesRead = fillInputBuffer(1);
/* 363 */       return (bytesRead < 0);
/* 364 */     } catch (SocketTimeoutException ex) {
/* 365 */       return false;
/* 366 */     } catch (IOException ex) {
/* 367 */       return true;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void incrementRequestCount() {
/* 372 */     this.connMetrics.incrementRequestCount();
/*     */   }
/*     */   
/*     */   protected void incrementResponseCount() {
/* 376 */     this.connMetrics.incrementResponseCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 381 */     return this.connMetrics;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 386 */     Socket socket = this.socketHolder.get();
/* 387 */     if (socket != null) {
/* 388 */       StringBuilder buffer = new StringBuilder();
/* 389 */       SocketAddress remoteAddress = socket.getRemoteSocketAddress();
/* 390 */       SocketAddress localAddress = socket.getLocalSocketAddress();
/* 391 */       if (remoteAddress != null && localAddress != null) {
/* 392 */         NetUtils.formatAddress(buffer, localAddress);
/* 393 */         buffer.append("<->");
/* 394 */         NetUtils.formatAddress(buffer, remoteAddress);
/*     */       } 
/* 396 */       return buffer.toString();
/*     */     } 
/* 398 */     return "[Not bound]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\BHttpConnectionBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */