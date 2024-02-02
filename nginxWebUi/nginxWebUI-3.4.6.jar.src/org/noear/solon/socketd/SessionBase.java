/*     */ package org.noear.solon.socketd;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.BiConsumer;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.message.Listener;
/*     */ import org.noear.solon.core.message.Message;
/*     */ import org.noear.solon.core.message.Session;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SessionBase
/*     */   implements Session
/*     */ {
/*  25 */   static final Logger log = LoggerFactory.getLogger(SessionBase.class);
/*     */   private String pathNew;
/*     */   
/*     */   public void pathNew(String pathNew) {
/*  29 */     this.pathNew = pathNew;
/*     */   }
/*     */ 
/*     */   
/*     */   public String pathNew() {
/*  34 */     if (this.pathNew == null) {
/*  35 */       return path();
/*     */     }
/*  37 */     return this.pathNew;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private int _flag = 0; private NvMap headerMap;
/*     */   
/*     */   public int flag() {
/*  47 */     return this._flag;
/*     */   }
/*     */   private NvMap paramMap;
/*     */   
/*     */   public void flagSet(int flag) {
/*  52 */     this._flag = flag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(String name) {
/*  60 */     return (String)headerMap().get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void headerSet(String name, String value) {
/*  65 */     headerMap().put(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public NvMap headerMap() {
/*  70 */     if (this.headerMap == null) {
/*  71 */       this.headerMap = new NvMap();
/*     */     }
/*     */     
/*  74 */     return this.headerMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String param(String name) {
/*  82 */     return (String)paramMap().get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void paramSet(String name, String value) {
/*  87 */     paramMap().put(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public NvMap paramMap() {
/*  92 */     if (this.paramMap == null) {
/*  93 */       this.paramMap = new NvMap();
/*     */       
/*  95 */       if (uri() != null) {
/*  96 */         String query = uri().getQuery();
/*     */         
/*  98 */         if (Utils.isNotEmpty(query)) {
/*  99 */           String[] ss = query.split("&");
/* 100 */           for (String kv : ss) {
/* 101 */             String[] s = kv.split("=");
/* 102 */             this.paramMap.put(s[0], s[1]);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 108 */     return this.paramMap;
/*     */   }
/*     */ 
/*     */   
/* 112 */   private Map<String, Object> attrMap = null;
/*     */   public Map<String, Object> attrMap() {
/* 114 */     if (this.attrMap == null) {
/* 115 */       this.attrMap = new HashMap<>();
/*     */     }
/*     */     
/* 118 */     return this.attrMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 124 */   private AtomicBoolean _handshaked = new AtomicBoolean();
/*     */   
/*     */   private Listener listener;
/*     */ 
/*     */   
/*     */   public void setHandshaked(boolean handshaked) {
/* 130 */     this._handshaked.set(handshaked);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getHandshaked() {
/* 137 */     return this._handshaked.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(Message message) {
/* 145 */     log.trace("Session send: {}", message);
/*     */   }
/*     */ 
/*     */   
/*     */   public String sendAndResponse(String message) {
/* 150 */     return sendAndResponse(Message.wrap(message)).bodyAsString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String sendAndResponse(String message, int timeout) {
/* 155 */     return sendAndResponse(Message.wrap(message), timeout).bodyAsString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message sendAndResponse(Message message) {
/* 163 */     return sendAndResponse(message, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message sendAndResponse(Message message, int timeout) {
/* 173 */     if (Utils.isEmpty(message.key())) {
/* 174 */       throw new IllegalArgumentException("SendAndResponse message no key");
/*     */     }
/*     */     
/* 177 */     if (timeout < 1) {
/* 178 */       timeout = RequestManager.REQUEST_AND_RESPONSE_TIMEOUT_SECONDS;
/*     */     }
/*     */ 
/*     */     
/* 182 */     CompletableFuture<Message> request = new CompletableFuture<>();
/* 183 */     RequestManager.register(message, request);
/*     */ 
/*     */     
/* 186 */     send(message);
/*     */ 
/*     */     
/*     */     try {
/* 190 */       return request.get(timeout, TimeUnit.SECONDS);
/* 191 */     } catch (Exception e) {
/* 192 */       throw new RuntimeException(e);
/*     */     } finally {
/* 194 */       RequestManager.remove(message.key());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendAndCallback(String message, BiConsumer<String, Throwable> callback) {
/* 200 */     sendAndCallback(Message.wrap(message), (msg, err) -> {
/*     */           if (msg == null) {
/*     */             callback.accept(null, err);
/*     */           } else {
/*     */             callback.accept(msg.bodyAsString(), err);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback) {
/* 214 */     if (Utils.isEmpty(message.key())) {
/* 215 */       throw new IllegalArgumentException("sendAndCallback message no key");
/*     */     }
/*     */ 
/*     */     
/* 219 */     CompletableFuture<Message> request = new CompletableFuture<>();
/* 220 */     RequestManager.register(message, request);
/*     */ 
/*     */     
/* 223 */     request.whenCompleteAsync(callback);
/*     */ 
/*     */     
/* 226 */     send(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Listener listener() {
/* 236 */     return this.listener;
/*     */   }
/*     */ 
/*     */   
/*     */   public void listener(Listener listener) {
/* 241 */     this.listener = listener;
/*     */   }
/*     */   
/*     */   protected void onOpen() {
/* 245 */     if (listener() != null) {
/* 246 */       listener().onOpen(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendHeartbeat() {
/* 256 */     send(Message.wrapHeartbeat());
/*     */   }
/*     */   
/*     */   private boolean _sendHeartbeatAuto = false;
/*     */   protected Message handshakeMessage;
/*     */   
/*     */   public void sendHeartbeatAuto(int intervalSeconds) {
/* 263 */     if (this._sendHeartbeatAuto) {
/*     */       return;
/*     */     }
/*     */     
/* 267 */     synchronized (this) {
/* 268 */       if (this._sendHeartbeatAuto) {
/*     */         return;
/*     */       }
/*     */       
/* 272 */       this._sendHeartbeatAuto = true;
/*     */       
/* 274 */       Utils.scheduled.scheduleWithFixedDelay(() -> {
/*     */             
/*     */             try {
/*     */               sendHeartbeat();
/* 278 */             } catch (Throwable ex) {
/*     */               EventBus.push(ex);
/*     */             } 
/*     */           }1L, intervalSeconds, TimeUnit.SECONDS);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendHandshake(Message message) {
/* 290 */     if (message.flag() == 12) {
/*     */       try {
/* 292 */         send(message);
/*     */       } finally {
/*     */         
/* 295 */         this.handshakeMessage = message;
/*     */       } 
/*     */     } else {
/* 298 */       throw new IllegalArgumentException("The message flag not handshake");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Message sendHandshakeAndResponse(Message message) {
/* 304 */     if (message.flag() == 12) {
/* 305 */       Message rst = sendAndResponse(message);
/*     */ 
/*     */       
/* 308 */       this.handshakeMessage = message;
/*     */       
/* 310 */       return rst;
/*     */     } 
/* 312 */     throw new IllegalArgumentException("The message flag not handshake");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SessionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */