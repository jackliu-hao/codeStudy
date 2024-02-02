/*     */ package org.noear.solon.core.route;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Endpoint;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Listener;
/*     */ import org.noear.solon.core.message.ListenerHolder;
/*     */ import org.noear.solon.core.message.Session;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RouterDefault
/*     */   implements Router
/*     */ {
/*     */   private final RoutingTable<Handler>[] routesH;
/*     */   private final RoutingTable<Listener> routesL;
/*     */   
/*     */   public RouterDefault() {
/*  26 */     this.routesH = (RoutingTable<Handler>[])new RoutingTableDefault[3];
/*     */     
/*  28 */     this.routesH[0] = new RoutingTableDefault<>();
/*  29 */     this.routesH[1] = new RoutingTableDefault<>();
/*  30 */     this.routesH[2] = new RoutingTableDefault<>();
/*     */     
/*  32 */     this.routesL = new RoutingTableDefault<>();
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
/*     */   public void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler) {
/*  46 */     RoutingDefault<Handler> routing = new RoutingDefault<>(path, method, index, handler);
/*     */     
/*  48 */     if (path.contains("*") || path.contains("{")) {
/*  49 */       this.routesH[endpoint.code].add(routing);
/*     */     } else {
/*     */       
/*  52 */       this.routesH[endpoint.code].add(0, routing);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(String pathPrefix) {
/*  58 */     this.routesH[Endpoint.before.code].remove(pathPrefix);
/*  59 */     this.routesH[Endpoint.main.code].remove(pathPrefix);
/*  60 */     this.routesH[Endpoint.after.code].remove(pathPrefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Routing<Handler>> getAll(Endpoint endpoint) {
/*  71 */     return this.routesH[endpoint.code].getAll();
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
/*     */   public Handler matchOne(Context ctx, Endpoint endpoint) {
/*  85 */     String pathNew = ctx.pathNew();
/*  86 */     MethodType method = MethodType.valueOf(ctx.method());
/*     */     
/*  88 */     return this.routesH[endpoint.code].matchOne(pathNew, method);
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
/*     */   public List<Handler> matchAll(Context ctx, Endpoint endpoint) {
/* 100 */     String pathNew = ctx.pathNew();
/* 101 */     MethodType method = MethodType.valueOf(ctx.method());
/*     */     
/* 103 */     return this.routesH[endpoint.code].matchAll(pathNew, method);
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
/*     */   public void add(String path, MethodType method, int index, Listener listener) {
/* 119 */     ListenerHolder listenerHolder = new ListenerHolder(path, listener);
/*     */     
/* 121 */     this.routesL.add(new RoutingDefault(path, method, index, listenerHolder));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Listener matchOne(Session session) {
/* 132 */     String path = session.pathNew();
/*     */     
/* 134 */     if (path == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     return this.routesL.matchOne(path, session.method());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Listener> matchAll(Session session) {
/* 143 */     String path = session.pathNew();
/*     */     
/* 145 */     if (path == null) {
/* 146 */       return null;
/*     */     }
/* 148 */     return this.routesL.matchAll(path, session.method());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 157 */     this.routesH[0].clear();
/* 158 */     this.routesH[1].clear();
/* 159 */     this.routesH[2].clear();
/*     */     
/* 161 */     this.routesL.clear();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RouterDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */