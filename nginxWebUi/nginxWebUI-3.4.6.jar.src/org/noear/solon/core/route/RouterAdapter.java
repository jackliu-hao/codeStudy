/*     */ package org.noear.solon.core.route;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.noear.solon.core.Aop;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.handle.Endpoint;
/*     */ import org.noear.solon.core.handle.Filter;
/*     */ import org.noear.solon.core.handle.FilterEntity;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.HandlerLoader;
/*     */ import org.noear.solon.core.handle.HandlerSlots;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Listener;
/*     */ import org.noear.solon.core.message.ListenerPipeline;
/*     */ 
/*     */ public abstract class RouterAdapter
/*     */   implements HandlerSlots {
/*     */   private Router _router;
/*     */   private RouterHandler _routerHandler;
/*  22 */   private List<FilterEntity> _filterList = new ArrayList<>();
/*     */ 
/*     */   
/*     */   protected void initRouter(Filter appFilter) {
/*  26 */     this._router = new RouterDefault();
/*  27 */     this._routerHandler = new RouterHandler(this._router);
/*  28 */     this._filterList.add(new FilterEntity(2147483647, appFilter));
/*     */   }
/*     */ 
/*     */   
/*     */   protected RouterHandler routerHandler() {
/*  33 */     return this._routerHandler;
/*     */   }
/*     */   
/*     */   protected List<FilterEntity> filterList() {
/*  37 */     return this._filterList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Router router() {
/*  44 */     return this._router;
/*     */   }
/*     */   
/*     */   public void routerSet(Router router) {
/*  48 */     if (router != null) {
/*  49 */       this._router = router;
/*  50 */       this._routerHandler.bind(router);
/*     */     } 
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
/*     */   public void filter(Filter filter) {
/*  67 */     filter(0, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void filter(int index, Filter filter) {
/*  78 */     this._filterList.add(new FilterEntity(index, filter));
/*  79 */     this._filterList.sort(Comparator.comparingInt(f -> f.index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(Handler handler) {
/*  86 */     before("**", MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(int index, Handler handler) {
/*  93 */     before("**", MethodType.ALL, index, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(MethodType method, Handler handler) {
/* 102 */     before("**", method, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(MethodType method, int index, Handler handler) {
/* 111 */     before("**", method, index, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(String expr, Handler handler) {
/* 118 */     before(expr, MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(String expr, MethodType method, Handler handler) {
/* 125 */     this._router.add(expr, Endpoint.before, method, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void before(String expr, MethodType method, int index, Handler handler) {
/* 133 */     this._router.add(expr, Endpoint.before, method, index, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void after(Handler handler) {
/* 140 */     after("**", MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void after(MethodType method, Handler handler) {
/* 149 */     after("**", method, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void after(String expr, Handler handler) {
/* 158 */     after(expr, MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void after(String expr, MethodType method, Handler handler) {
/* 165 */     this._router.add(expr, Endpoint.after, method, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void after(String expr, MethodType method, int index, Handler handler) {
/* 173 */     this._router.add(expr, Endpoint.after, method, index, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(String expr, MethodType method, Handler handler) {
/* 181 */     this._router.add(expr, Endpoint.main, method, handler);
/*     */   }
/*     */   
/*     */   public void add(String expr, Class<?> clz) {
/* 185 */     BeanWrap bw = Aop.wrapAndPut(clz);
/* 186 */     if (bw != null) {
/* 187 */       (new HandlerLoader(bw, expr)).load(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public void add(String expr, Class<?> clz, boolean remoting) {
/* 192 */     BeanWrap bw = Aop.wrapAndPut(clz);
/* 193 */     if (bw != null) {
/* 194 */       (new HandlerLoader(bw, expr, remoting)).load(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void all(String path, Handler handler) {
/* 203 */     add(path, MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void http(String path, Handler handler) {
/* 210 */     add(path, MethodType.HTTP, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void head(String path, Handler handler) {
/* 217 */     add(path, MethodType.HEAD, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void get(String path, Handler handler) {
/* 224 */     add(path, MethodType.GET, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void post(String path, Handler handler) {
/* 231 */     add(path, MethodType.POST, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(String path, Handler handler) {
/* 238 */     add(path, MethodType.PUT, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void patch(String path, Handler handler) {
/* 245 */     add(path, MethodType.PATCH, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void delete(String path, Handler handler) {
/* 252 */     add(path, MethodType.DELETE, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ws(String path, Handler handler) {
/* 259 */     add(path, MethodType.WEBSOCKET, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ws(String path, Listener listener) {
/* 266 */     this._router.add(path, MethodType.WEBSOCKET, listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void socket(String path, Handler handler) {
/* 273 */     add(path, MethodType.SOCKET, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void socket(String path, Listener listener) {
/* 280 */     this._router.add(path, MethodType.SOCKET, listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listen(String path, Listener listener) {
/* 287 */     this._router.add(path, MethodType.ALL, listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listenBefore(Listener listener) {
/* 294 */     this._listenerPipeline.prev(listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listenAfter(Listener listener) {
/* 301 */     this._listenerPipeline.next(listener);
/*     */   }
/*     */   
/* 304 */   private final ListenerPipeline _listenerPipeline = new ListenerPipeline();
/*     */ 
/*     */ 
/*     */   
/*     */   public Listener listener() {
/* 309 */     return (Listener)this._listenerPipeline;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RouterAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */