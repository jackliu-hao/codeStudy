/*     */ package org.noear.solon.boot.undertow.websocket;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSockets;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Message;
/*     */ import org.noear.solon.core.message.Session;
/*     */ import org.noear.solon.socketd.ProtocolManager;
/*     */ 
/*     */ public class _SocketServerSession extends SessionBase {
/*  21 */   public static final Map<WebSocketChannel, Session> sessions = new HashMap<>(); private final WebSocketChannel real;
/*     */   public static Session get(WebSocketChannel real) {
/*     */     _SocketServerSession _SocketServerSession1;
/*  24 */     Session tmp = sessions.get(real);
/*  25 */     if (tmp == null) {
/*  26 */       synchronized (real) {
/*  27 */         tmp = sessions.get(real);
/*  28 */         if (tmp == null) {
/*  29 */           _SocketServerSession1 = new _SocketServerSession(real);
/*  30 */           sessions.put(real, _SocketServerSession1);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  35 */     return (Session)_SocketServerSession1;
/*     */   }
/*     */   
/*     */   public static void remove(WebSocketChannel real) {
/*  39 */     sessions.remove(real);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  44 */   private final String _sessionId = Utils.guid(); private URI _uri; private String _path;
/*     */   
/*     */   public _SocketServerSession(WebSocketChannel real) {
/*  47 */     this.real = real;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object real() {
/*  52 */     return this.real;
/*     */   }
/*     */ 
/*     */   
/*     */   public String sessionId() {
/*  57 */     return this._sessionId;
/*     */   }
/*     */ 
/*     */   
/*     */   public MethodType method() {
/*  62 */     return MethodType.WEBSOCKET;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI uri() {
/*  69 */     if (this._uri == null) {
/*  70 */       this._uri = URI.create(this.real.getUrl());
/*     */     }
/*     */     
/*  73 */     return this._uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String path() {
/*  80 */     if (this._path == null) {
/*  81 */       this._path = uri().getPath();
/*     */     }
/*     */     
/*  84 */     return this._path;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendAsync(String message) {
/*  89 */     if (Solon.app().enableWebSocketD()) {
/*  90 */       ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
/*  91 */       WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
/*     */     } else {
/*  93 */       WebSockets.sendText(message, this.real, _CallbackImpl.instance);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendAsync(Message message) {
/*  99 */     super.send(message);
/*     */     
/* 101 */     if (Solon.app().enableWebSocketD()) {
/* 102 */       ByteBuffer buf = ProtocolManager.encode(message);
/* 103 */       WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
/*     */     }
/* 105 */     else if (message.isString()) {
/* 106 */       WebSockets.sendText(message.bodyAsString(), this.real, _CallbackImpl.instance);
/*     */     } else {
/* 108 */       ByteBuffer buf = ByteBuffer.wrap(message.body());
/* 109 */       WebSockets.sendBinary(buf, this.real, _CallbackImpl.instance);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(String message) {
/*     */     try {
/* 117 */       if (Solon.app().enableWebSocketD()) {
/* 118 */         ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
/* 119 */         WebSockets.sendBinaryBlocking(buf, this.real);
/*     */       } else {
/* 121 */         WebSockets.sendTextBlocking(message, this.real);
/*     */       } 
/* 123 */     } catch (RuntimeException e) {
/* 124 */       throw e;
/* 125 */     } catch (Throwable e) {
/* 126 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(Message message) {
/* 132 */     super.send(message);
/*     */     
/*     */     try {
/* 135 */       if (Solon.app().enableWebSocketD()) {
/* 136 */         ByteBuffer buf = ProtocolManager.encode(message);
/* 137 */         WebSockets.sendBinaryBlocking(buf, this.real);
/*     */       }
/* 139 */       else if (message.isString()) {
/* 140 */         WebSockets.sendTextBlocking(message.bodyAsString(), this.real);
/*     */       } else {
/* 142 */         ByteBuffer buf = ByteBuffer.wrap(message.body());
/* 143 */         WebSockets.sendBinaryBlocking(buf, this.real);
/*     */       }
/*     */     
/* 146 */     } catch (RuntimeException e) {
/* 147 */       throw e;
/* 148 */     } catch (Throwable e) {
/* 149 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 156 */     if (this.real == null) {
/*     */       return;
/*     */     }
/*     */     
/* 160 */     this.real.close();
/* 161 */     sessions.remove(this.real);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid() {
/* 166 */     if (this.real == null) {
/* 167 */       return false;
/*     */     }
/*     */     
/* 170 */     return this.real.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 175 */     return this.real.isSecure();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress getRemoteAddress() {
/* 180 */     return this.real.getSourceAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress getLocalAddress() {
/* 185 */     return this.real.getDestinationAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttachment(Object obj) {
/* 190 */     this.real.setAttribute("attachment", obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getAttachment() {
/* 195 */     return (T)this.real.getAttribute("attachment");
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<Session> getOpenSessions() {
/* 200 */     return Collections.unmodifiableCollection(sessions.values());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 206 */     if (this == o) return true; 
/* 207 */     if (o == null || getClass() != o.getClass()) return false; 
/* 208 */     _SocketServerSession that = (_SocketServerSession)o;
/* 209 */     return Objects.equals(this.real, that.real);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 214 */     return Objects.hash(new Object[] { this.real });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\websocket\_SocketServerSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */