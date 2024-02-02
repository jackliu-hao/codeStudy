/*     */ package org.noear.solon.socketd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import org.noear.solon.Solon;
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
/*     */ 
/*     */ 
/*     */ public class RouterListener
/*     */   implements Listener
/*     */ {
/*  24 */   static final Logger log = LoggerFactory.getLogger(RouterListener.class);
/*     */ 
/*     */ 
/*     */   
/*  28 */   static final ExecutorService executors = Executors.newCachedThreadPool();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onOpen(Session session) {
/*  39 */     executors.submit(() -> onOpen0(session));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onOpen0(Session session) {
/*     */     try {
/*  47 */       Listener sl = get(session);
/*  48 */       if (sl != null) {
/*  49 */         sl.onOpen(session);
/*     */       }
/*     */ 
/*     */       
/*  53 */       if (session.listener() != null) {
/*  54 */         session.listener().onOpen(session);
/*     */       }
/*  56 */     } catch (Throwable ex) {
/*  57 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onMessage(Session session, Message message) throws IOException {
/*  66 */     if (message == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     executors.submit(() -> onMessage0(session, message));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void onMessage0(Session session, Message message) {
/*     */     try {
/*  80 */       log.trace("Listener proxy receive: {}", message);
/*     */ 
/*     */       
/*  83 */       Listener sl = get(session);
/*  84 */       if (sl != null) {
/*  85 */         sl.onMessage(session, message);
/*     */       }
/*     */ 
/*     */       
/*  89 */       if (session.listener() != null) {
/*  90 */         session.listener().onMessage(session, message);
/*     */       }
/*     */ 
/*     */       
/*  94 */       if (message.flag() == 11) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*  99 */       if (message.flag() == 13) {
/*     */ 
/*     */         
/* 102 */         CompletableFuture<Message> request = RequestManager.get(message.key());
/*     */ 
/*     */         
/* 105 */         if (request != null) {
/* 106 */           RequestManager.remove(message.key());
/* 107 */           request.complete(message);
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */       
/* 113 */       if (!message.getHandled()) {
/* 114 */         SocketContextHandler.instance.handle(session, message);
/*     */       }
/* 116 */     } catch (Throwable ex) {
/* 117 */       onError0(session, ex);
/* 118 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onClose(Session session) {
/* 127 */     executors.submit(() -> onClose0(session));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onClose0(Session session) {
/*     */     try {
/* 135 */       Listener sl = get(session);
/* 136 */       if (sl != null) {
/* 137 */         sl.onClose(session);
/*     */       }
/*     */ 
/*     */       
/* 141 */       if (session.listener() != null) {
/* 142 */         session.listener().onClose(session);
/*     */       }
/* 144 */     } catch (Throwable ex) {
/* 145 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onError(Session session, Throwable error) {
/* 154 */     executors.submit(() -> onError0(session, error));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onError0(Session session, Throwable error) {
/*     */     try {
/* 162 */       Listener sl = get(session);
/* 163 */       if (sl != null) {
/* 164 */         sl.onError(session, error);
/*     */       }
/*     */ 
/*     */       
/* 168 */       if (session.listener() != null) {
/* 169 */         session.listener().onError(session, error);
/*     */       }
/* 171 */     } catch (Throwable ex) {
/* 172 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Listener get(Session s) {
/* 182 */     return Solon.app().router().matchOne(s);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\RouterListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */