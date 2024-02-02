/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.annotation.Note;
/*     */ import org.noear.solon.core.Aop;
/*     */ import org.noear.solon.core.AopContext;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.route.Routing;
/*     */ import org.noear.solon.core.route.RoutingDefault;
/*     */ import org.noear.solon.core.route.RoutingTable;
/*     */ import org.noear.solon.core.route.RoutingTableDefault;
/*     */ import org.noear.solon.core.util.PathUtil;
/*     */ import org.noear.solon.ext.DataThrowable;
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
/*     */ public abstract class Gateway
/*     */   extends HandlerAide
/*     */   implements Handler, Render
/*     */ {
/*     */   private Handler mainDef;
/*     */   private final RoutingTable<Handler> mainRouting;
/*     */   private final String mapping;
/*     */   private Mapping mappingAnno;
/*  46 */   private List<FilterEntity> filterList = new ArrayList<>();
/*     */   
/*     */   public Gateway() {
/*  49 */     this((RoutingTable<Handler>)new RoutingTableDefault());
/*     */   }
/*     */ 
/*     */   
/*     */   public Gateway(RoutingTable<Handler> routingTable) {
/*  54 */     this.mainRouting = routingTable;
/*     */     
/*  56 */     this.mappingAnno = getClass().<Mapping>getAnnotation(Mapping.class);
/*  57 */     if (this.mappingAnno == null) {
/*  58 */       throw new IllegalStateException("No Mapping!");
/*     */     }
/*     */     
/*  61 */     this.mapping = Utils.annoAlias(this.mappingAnno.value(), this.mappingAnno.path());
/*     */ 
/*     */     
/*  64 */     this.mainDef = (c -> c.status(404));
/*     */     
/*  66 */     this.filterList.add(new FilterEntity(2147483647, this::doFilter));
/*     */     
/*  68 */     register();
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
/*     */   @Note("允许 Action Mapping 申明")
/*     */   protected boolean allowActionMapping() {
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("充许提前准备控制器")
/*     */   protected boolean allowReadyController() {
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("充许路径合并")
/*     */   protected boolean allowPathMerging() {
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void render(Object obj, Context c) throws Throwable {
/* 108 */     if (c.getRendered()) {
/*     */       return;
/*     */     }
/*     */     
/* 112 */     if (obj instanceof DataThrowable) {
/*     */       return;
/*     */     }
/*     */     
/* 116 */     c.result = obj;
/* 117 */     c.render(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void filter(Filter filter) {
/* 126 */     filter(0, filter);
/*     */   }
/*     */   
/*     */   public void filter(int index, Filter filter) {
/* 130 */     this.filterList.add(new FilterEntity(index, filter));
/* 131 */     this.filterList.sort(Comparator.comparingInt(f -> f.index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(Context c) throws Throwable {
/*     */     try {
/* 140 */       (new FilterChainNode(this.filterList)).doFilter(c);
/* 141 */     } catch (Throwable e) {
/* 142 */       c.setHandled(true);
/*     */       
/* 144 */       e = Utils.throwableUnwrap(e);
/*     */       
/* 146 */       if (e instanceof DataThrowable) {
/* 147 */         DataThrowable ex = (DataThrowable)e;
/*     */         
/* 149 */         if (ex.data() == null) {
/* 150 */           render(ex, c);
/*     */         } else {
/* 152 */           render(ex.data(), c);
/*     */         } 
/*     */       } else {
/* 155 */         c.errors = e;
/*     */ 
/*     */         
/* 158 */         EventBus.push(e);
/*     */ 
/*     */         
/* 161 */         if (c.result == null) {
/* 162 */           render(e, c);
/*     */         } else {
/* 164 */           render(c.result, c);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void doFilter(Context c, FilterChain chain) throws Throwable {
/* 171 */     Handler m = find(c);
/* 172 */     Object obj = null;
/*     */ 
/*     */     
/* 175 */     if (m != null) {
/* 176 */       Boolean is_action = Boolean.valueOf(m instanceof Action);
/*     */       
/* 178 */       if (is_action.booleanValue()) {
/* 179 */         if (allowReadyController()) {
/*     */           
/* 181 */           obj = ((Action)m).controller().get();
/* 182 */           c.attrSet("controller", obj);
/*     */         } 
/*     */         
/* 185 */         c.attrSet("action", m);
/*     */       } 
/*     */       
/* 188 */       handle0(c, m, obj, is_action);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void handle0(Context c, Handler m, Object obj, Boolean is_action) throws Throwable {
/*     */     try {
/* 199 */       for (Handler h : this.befores) {
/* 200 */         h.handle(c);
/*     */       }
/*     */ 
/*     */       
/* 204 */       if (!c.getHandled()) {
/* 205 */         if (is_action.booleanValue()) {
/* 206 */           ((Action)m).invoke(c, obj);
/*     */         } else {
/* 208 */           m.handle(c);
/*     */         } 
/*     */       } else {
/* 211 */         render(c.result, c);
/*     */       } 
/* 213 */     } catch (Throwable e) {
/* 214 */       e = Utils.throwableUnwrap(e);
/* 215 */       if (e instanceof DataThrowable) {
/* 216 */         DataThrowable ex = (DataThrowable)e;
/* 217 */         if (ex.data() == null) {
/* 218 */           render(ex, c);
/*     */         } else {
/* 220 */           render(ex.data(), c);
/*     */         } 
/*     */       } else {
/* 223 */         c.errors = e;
/* 224 */         throw e;
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 229 */       for (Handler h : this.afters) {
/* 230 */         h.handle(c);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加前置拦截器")
/*     */   public <T extends Handler> void before(Class<T> interceptorClz) {
/* 241 */     before((Handler)Aop.getOrNew(interceptorClz));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加后置拦截器")
/*     */   public <T extends Handler> void after(Class<T> interceptorClz) {
/* 250 */     after((Handler)Aop.getOrNew(interceptorClz));
/*     */   }
/*     */   
/*     */   @Note("添加接口")
/*     */   public void addBeans(Predicate<BeanWrap> where) {
/* 255 */     addBeans(where, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加接口")
/*     */   public void addBeans(Predicate<BeanWrap> where, boolean remoting) {
/* 263 */     Aop.context().beanOnloaded(ctx -> ctx.beanForeach(()));
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
/*     */   @Note("添加接口")
/*     */   public void add(Class<?> beanClz) {
/* 281 */     if (beanClz != null) {
/* 282 */       BeanWrap bw = Aop.wrapAndPut(beanClz);
/*     */       
/* 284 */       add(bw, bw.remoting());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(String path, Class<?> beanClz) {
/* 293 */     if (beanClz != null) {
/* 294 */       BeanWrap bw = Aop.wrapAndPut(beanClz);
/*     */       
/* 296 */       add(path, bw, bw.remoting());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(Class<?> beanClz, boolean remoting) {
/* 305 */     if (beanClz != null) {
/* 306 */       add(Aop.wrapAndPut(beanClz), remoting);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(String path, Class<?> beanClz, boolean remoting) {
/* 315 */     if (beanClz != null) {
/* 316 */       add(path, Aop.wrapAndPut(beanClz), remoting);
/*     */     }
/*     */   }
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(BeanWrap beanWp) {
/* 322 */     add(beanWp, beanWp.remoting());
/*     */   }
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(String path, BeanWrap beanWp) {
/* 327 */     add(path, beanWp, beanWp.remoting());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(BeanWrap beanWp, boolean remoting) {
/* 335 */     add((String)null, beanWp, remoting);
/*     */   }
/*     */   
/*     */   @Note("添加接口")
/*     */   public void add(String path, BeanWrap beanWp, boolean remoting) {
/* 340 */     if (beanWp == null) {
/*     */       return;
/*     */     }
/*     */     
/* 344 */     Mapping bMapping = (Mapping)beanWp.clz().getAnnotation(Mapping.class);
/* 345 */     String bPath = null;
/* 346 */     if (bMapping != null) {
/* 347 */       bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
/*     */     }
/*     */     
/* 350 */     HandlerLoader uw = new HandlerLoader(beanWp, bPath, remoting, this, allowActionMapping());
/*     */     
/* 352 */     uw.load((expr, method, handler) -> {
/*     */           if (path == null) {
/*     */             addDo(expr, method, handler);
/*     */           } else {
/*     */             addDo(PathUtil.mergePath(path, expr), method, handler);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @Note("添加缺少处理")
/*     */   public void add(Handler handler) {
/* 364 */     addDo("", MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加二级路径处理")
/*     */   public void add(String path, Handler handler) {
/* 372 */     addDo(path, MethodType.ALL, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加二级路径处理")
/*     */   public void add(String path, MethodType method, Handler handler) {
/* 381 */     addDo(path, method, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addDo(String path, MethodType method, Handler handler) {
/* 388 */     if (Utils.isEmpty(path) || "/".equals(path)) {
/* 389 */       this.mainDef = handler;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 394 */     if (allowPathMerging()) {
/* 395 */       String path2 = PathUtil.mergePath(this.mapping, path);
/* 396 */       this.mainRouting.add((Routing)new RoutingDefault(path2, method, handler));
/*     */     } else {
/* 398 */       this.mainRouting.add((Routing)new RoutingDefault(path, method, handler));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Handler getDo(Context c, String path) {
/* 406 */     if (path == null) {
/* 407 */       return null;
/*     */     }
/* 409 */     MethodType method = MethodType.valueOf(c.method());
/* 410 */     return (Handler)this.mainRouting.matchOne(path, method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Handler find(Context c) throws Throwable {
/* 418 */     return findDo(c, c.pathNew());
/*     */   }
/*     */   
/*     */   protected Handler findDo(Context c, String path) throws Throwable {
/* 422 */     Handler h = getDo(c, path);
/*     */     
/* 424 */     if (h == null) {
/* 425 */       this.mainDef.handle(c);
/* 426 */       c.setHandled(true);
/* 427 */       return this.mainDef;
/*     */     } 
/* 429 */     if (h instanceof Action) {
/* 430 */       c.attrSet("handler_name", ((Action)h).fullName());
/*     */     }
/* 432 */     return h;
/*     */   }
/*     */   
/*     */   @Note("注册相关接口与拦截器")
/*     */   protected abstract void register();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Gateway.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */