/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.Socket;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.impl.SocketHttpClientConnection;
/*     */ import org.apache.http.io.HttpMessageParser;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.params.HttpProtocolParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Deprecated
/*     */ public class DefaultClientConnection
/*     */   extends SocketHttpClientConnection
/*     */   implements OperatedClientConnection, ManagedHttpClientConnection, HttpContext
/*     */ {
/*  70 */   private final Log log = LogFactory.getLog(getClass());
/*  71 */   private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
/*  72 */   private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
/*     */ 
/*     */   
/*     */   private volatile Socket socket;
/*     */ 
/*     */   
/*     */   private HttpHost targetHost;
/*     */ 
/*     */   
/*     */   private boolean connSecure;
/*     */ 
/*     */   
/*     */   private volatile boolean shutdown;
/*     */ 
/*     */   
/*     */   private final Map<String, Object> attributes;
/*     */ 
/*     */   
/*     */   public DefaultClientConnection() {
/*  91 */     this.attributes = new HashMap<String, Object>();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getTargetHost() {
/* 101 */     return this.targetHost;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isSecure() {
/* 106 */     return this.connSecure;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Socket getSocket() {
/* 111 */     return this.socket;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 116 */     if (this.socket instanceof SSLSocket) {
/* 117 */       return ((SSLSocket)this.socket).getSession();
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void opening(Socket sock, HttpHost target) throws IOException {
/* 125 */     assertNotOpen();
/* 126 */     this.socket = sock;
/* 127 */     this.targetHost = target;
/*     */ 
/*     */     
/* 130 */     if (this.shutdown) {
/* 131 */       sock.close();
/*     */       
/* 133 */       throw new InterruptedIOException("Connection already shutdown");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void openCompleted(boolean secure, HttpParams params) throws IOException {
/* 139 */     Args.notNull(params, "Parameters");
/* 140 */     assertNotOpen();
/* 141 */     this.connSecure = secure;
/* 142 */     bind(this.socket, params);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 160 */     this.shutdown = true;
/*     */     try {
/* 162 */       super.shutdown();
/* 163 */       if (this.log.isDebugEnabled()) {
/* 164 */         this.log.debug("Connection " + this + " shut down");
/*     */       }
/* 166 */       Socket sock = this.socket;
/* 167 */       if (sock != null) {
/* 168 */         sock.close();
/*     */       }
/* 170 */     } catch (IOException ex) {
/* 171 */       this.log.debug("I/O error shutting down connection", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 178 */       super.close();
/* 179 */       if (this.log.isDebugEnabled()) {
/* 180 */         this.log.debug("Connection " + this + " closed");
/*     */       }
/* 182 */     } catch (IOException ex) {
/* 183 */       this.log.debug("I/O error closing connection", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SessionInputBuffer createSessionInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/* 192 */     SessionInputBuffer inBuffer = super.createSessionInputBuffer(socket, (bufferSize > 0) ? bufferSize : 8192, params);
/*     */ 
/*     */ 
/*     */     
/* 196 */     if (this.wireLog.isDebugEnabled()) {
/* 197 */       inBuffer = new LoggingSessionInputBuffer(inBuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 202 */     return inBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/* 210 */     SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, (bufferSize > 0) ? bufferSize : 8192, params);
/*     */ 
/*     */ 
/*     */     
/* 214 */     if (this.wireLog.isDebugEnabled()) {
/* 215 */       outbuffer = new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 220 */     return outbuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
/* 229 */     return (HttpMessageParser<HttpResponse>)new DefaultHttpResponseParser(buffer, null, responseFactory, params);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 235 */     bind(socket, (HttpParams)new BasicHttpParams());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
/* 243 */     assertOpen();
/* 244 */     Args.notNull(target, "Target host");
/* 245 */     Args.notNull(params, "Parameters");
/*     */     
/* 247 */     if (sock != null) {
/* 248 */       this.socket = sock;
/* 249 */       bind(sock, params);
/*     */     } 
/* 251 */     this.targetHost = target;
/* 252 */     this.connSecure = secure;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 257 */     HttpResponse response = super.receiveResponseHeader();
/* 258 */     if (this.log.isDebugEnabled()) {
/* 259 */       this.log.debug("Receiving response: " + response.getStatusLine());
/*     */     }
/* 261 */     if (this.headerLog.isDebugEnabled()) {
/* 262 */       this.headerLog.debug("<< " + response.getStatusLine().toString());
/* 263 */       Header[] headers = response.getAllHeaders();
/* 264 */       for (Header header : headers) {
/* 265 */         this.headerLog.debug("<< " + header.toString());
/*     */       }
/*     */     } 
/* 268 */     return response;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 273 */     if (this.log.isDebugEnabled()) {
/* 274 */       this.log.debug("Sending request: " + request.getRequestLine());
/*     */     }
/* 276 */     super.sendRequestHeader(request);
/* 277 */     if (this.headerLog.isDebugEnabled()) {
/* 278 */       this.headerLog.debug(">> " + request.getRequestLine().toString());
/* 279 */       Header[] headers = request.getAllHeaders();
/* 280 */       for (Header header : headers) {
/* 281 */         this.headerLog.debug(">> " + header.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String id) {
/* 288 */     return this.attributes.get(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 293 */     return this.attributes.remove(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 298 */     this.attributes.put(id, obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */