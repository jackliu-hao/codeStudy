/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.net.URI;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.core.message.Session;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SessionFactoryManager
/*    */ {
/* 16 */   public static Map<String, SessionFactory> uriCached = new HashMap<>();
/* 17 */   public static Map<Class<?>, SessionFactory> clzCached = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void register(SessionFactory factory) {
/* 23 */     for (String p : factory.schemes()) {
/* 24 */       uriCached.putIfAbsent(p, factory);
/*    */     }
/*    */     
/* 27 */     clzCached.putIfAbsent(factory.driveType(), factory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Session create(Connector connector) {
/* 36 */     SessionFactory factory = clzCached.get(connector.driveType());
/*    */     
/* 38 */     if (factory == null) {
/* 39 */       throw new IllegalArgumentException("The connector is not supported");
/*    */     }
/*    */     
/* 42 */     Session session = factory.createSession(connector);
/* 43 */     session.setHandshaked(true);
/*    */     
/* 45 */     return session;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Session create(URI uri, boolean autoReconnect) {
/* 55 */     SessionFactory factory = uriCached.get(uri.getScheme());
/*    */     
/* 57 */     if (factory == null) {
/* 58 */       throw new IllegalArgumentException("The " + uri.getScheme() + " protocol is not supported");
/*    */     }
/*    */     
/* 61 */     Session session = factory.createSession(uri, autoReconnect);
/* 62 */     session.setHandshaked(true);
/*    */     
/* 64 */     return session;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SessionFactoryManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */