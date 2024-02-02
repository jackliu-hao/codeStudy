/*     */ package org.noear.solon.socketd;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.ContextEmpty;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Message;
/*     */ import org.noear.solon.core.message.Session;
/*     */ import org.noear.solon.socketd.util.HeaderUtil;
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
/*     */ public class SocketContext
/*     */   extends ContextEmpty
/*     */ {
/*     */   private InetSocketAddress _inetSocketAddress;
/*     */   private Session _session;
/*     */   private Message _message;
/*     */   private boolean _messageIsString;
/*     */   private MethodType _method;
/*     */   private URI _uri;
/*     */   ByteArrayOutputStream _outputStream;
/*     */   
/*     */   public Object request() {
/*     */     return this._session;
/*     */   }
/*     */   
/*     */   public String ip() {
/*     */     if (this._inetSocketAddress == null)
/*     */       return null; 
/*     */     return this._inetSocketAddress.getAddress().toString();
/*     */   }
/*     */   
/*     */   public boolean isMultipart() {
/*     */     return false;
/*     */   }
/*     */   
/*     */   public String method() {
/*     */     return this._method.name;
/*     */   }
/*     */   
/*     */   public String protocol() {
/*     */     if (this._method == MethodType.WEBSOCKET)
/*     */       return "WS"; 
/*     */     return "SOCKET";
/*     */   }
/*     */   
/*     */   public URI uri() {
/*     */     if (this._uri == null)
/*     */       this._uri = URI.create(url()); 
/*     */     return this._uri;
/*     */   }
/*     */   
/*     */   public SocketContext(Session session, Message message) {
/* 139 */     this._outputStream = new ByteArrayOutputStream(); this._session = session; this._message = message; this._messageIsString = message.isString(); this._method = session.method(); this._inetSocketAddress = session.getRemoteAddress(); if (session.headerMap().size() > 0)
/*     */       headerMap().putAll((Map)session.headerMap());  if (Utils.isNotEmpty(message.header())) {
/*     */       Map<String, String> headerMap = HeaderUtil.decodeHeaderMap(message.header()); headerMap().putAll(headerMap);
/*     */     }  if (session.paramMap().size() > 0)
/* 143 */       paramMap().putAll((Map)session.paramMap());  this.sessionState = new SocketSessionState(this._session); } public OutputStream outputStream() { return this._outputStream; } public String url() { return this._message.resourceDescriptor(); }
/*     */   public long contentLength() { if (this._message.body() == null)
/*     */       return 0L; 
/*     */     return (this._message.body()).length; }
/*     */   public String contentType() { return (String)headerMap().get("Content-Type"); }
/*     */   public void output(byte[] bytes) { try {
/* 149 */       this._outputStream.write(bytes);
/* 150 */     } catch (Exception ex) {
/* 151 */       throw new RuntimeException(ex);
/*     */     }  }
/*     */   public String queryString() {
/*     */     return uri().getQuery();
/*     */   } public InputStream bodyAsStream() throws IOException {
/*     */     return new ByteArrayInputStream(this._message.body());
/*     */   } public void output(InputStream stream) { try {
/* 158 */       byte[] buff = new byte[100];
/* 159 */       int rc = 0;
/* 160 */       while ((rc = stream.read(buff, 0, 100)) > 0) {
/* 161 */         this._outputStream.write(buff, 0, rc);
/*     */       }
/*     */     }
/* 164 */     catch (Throwable ex) {
/* 165 */       throw new RuntimeException(ex);
/*     */     }  } public Object response() {
/*     */     return this._session;
/*     */   } public void contentType(String contentType) {
/*     */     headerSet("Content-Type", contentType);
/*     */   } protected void commit() throws IOException {
/* 171 */     if (this._session.isValid()) {
/* 172 */       if (this._messageIsString) {
/* 173 */         this._session.send(this._outputStream.toString());
/*     */       } else {
/* 175 */         Message msg = Message.wrapResponse(this._message, this._outputStream.toByteArray());
/* 176 */         this._session.send(msg);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 183 */     this._session.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SocketContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */