/*     */ package org.noear.solon.core.route;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Endpoint;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Listener;
/*     */ import org.noear.solon.core.message.Session;
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
/*     */ public interface Router
/*     */ {
/*     */   default void add(String path, Handler handler) {
/*  51 */     add(path, Endpoint.main, MethodType.HTTP, handler);
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
/*     */   default void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
/*  63 */     add(path, endpoint, method, 0, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void add(String paramString, Endpoint paramEndpoint, MethodType paramMethodType, int paramInt, Handler paramHandler);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void remove(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Collection<Routing<Handler>> getAll(Endpoint paramEndpoint);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Handler matchOne(Context paramContext, Endpoint paramEndpoint);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List<Handler> matchAll(Context paramContext, Endpoint paramEndpoint);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void add(String path, Listener listener) {
/* 117 */     add(path, MethodType.ALL, listener);
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
/*     */   default void add(String path, MethodType method, Listener listener) {
/* 129 */     add(path, method, 0, listener);
/*     */   }
/*     */   
/*     */   void add(String paramString, MethodType paramMethodType, int paramInt, Listener paramListener);
/*     */   
/*     */   Listener matchOne(Session paramSession);
/*     */   
/*     */   List<Listener> matchAll(Session paramSession);
/*     */   
/*     */   void clear();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\Router.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */