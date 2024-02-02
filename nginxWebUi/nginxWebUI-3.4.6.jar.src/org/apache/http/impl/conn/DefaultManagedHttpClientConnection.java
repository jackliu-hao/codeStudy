/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.Socket;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.DefaultBHttpClientConnection;
/*     */ import org.apache.http.io.HttpMessageParserFactory;
/*     */ import org.apache.http.io.HttpMessageWriterFactory;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ public class DefaultManagedHttpClientConnection
/*     */   extends DefaultBHttpClientConnection
/*     */   implements ManagedHttpClientConnection, HttpContext
/*     */ {
/*     */   private final String id;
/*     */   private final Map<String, Object> attributes;
/*     */   private volatile boolean shutdown;
/*     */   
/*     */   public DefaultManagedHttpClientConnection(String id, int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
/*  74 */     super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
/*     */ 
/*     */     
/*  77 */     this.id = id;
/*  78 */     this.attributes = new ConcurrentHashMap<String, Object>();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultManagedHttpClientConnection(String id, int bufferSize) {
/*  84 */     this(id, bufferSize, bufferSize, null, null, null, null, null, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/*  89 */     return this.id;
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/*  94 */     this.shutdown = true;
/*  95 */     super.shutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String id) {
/* 100 */     return this.attributes.get(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 105 */     return this.attributes.remove(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 110 */     this.attributes.put(id, obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 115 */     if (this.shutdown) {
/* 116 */       socket.close();
/*     */       
/* 118 */       throw new InterruptedIOException("Connection already shutdown");
/*     */     } 
/* 120 */     super.bind(socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket getSocket() {
/* 125 */     return super.getSocket();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 130 */     Socket socket = super.getSocket();
/* 131 */     return (socket instanceof SSLSocket) ? ((SSLSocket)socket).getSession() : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultManagedHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */