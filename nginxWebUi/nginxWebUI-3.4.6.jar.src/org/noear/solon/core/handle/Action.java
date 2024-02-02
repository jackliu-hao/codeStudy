/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Consumes;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.annotation.Produces;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.Bridge;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.util.PathAnalyzer;
/*     */ import org.noear.solon.core.util.PathUtil;
/*     */ import org.noear.solon.core.wrap.MethodWrap;
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
/*     */ public class Action
/*     */   extends HandlerAide
/*     */   implements Handler
/*     */ {
/*     */   private final BeanWrap bWrap;
/*     */   private final HandlerAide bAide;
/*     */   private Render bRender;
/*     */   private final boolean mIsMain;
/*     */   private final MethodWrap mWrap;
/*     */   private String mProduces;
/*     */   private String mConsumes;
/*     */   private final String mName;
/*     */   private final String mFullName;
/*     */   private final boolean mRemoting;
/*     */   private final Mapping mMapping;
/*     */   private boolean mMultipart;
/*     */   private PathAnalyzer pathAnalyzer;
/*     */   private List<String> pathKeys;
/*     */   
/*     */   public Action(BeanWrap bWrap, Method method) {
/*  56 */     this(bWrap, (HandlerAide)null, method, (Mapping)null, (String)null, false, (Render)null);
/*     */   }
/*     */   
/*     */   public Action(BeanWrap bWrap, HandlerAide bAide, Method method, Mapping mapping, String path, boolean remoting, Render render) {
/*  60 */     this.bWrap = bWrap;
/*  61 */     this.bAide = bAide;
/*     */     
/*  63 */     method.setAccessible(true);
/*     */     
/*  65 */     this.mWrap = bWrap.context().methodGet(method);
/*  66 */     this.mRemoting = remoting;
/*  67 */     this.mMapping = mapping;
/*  68 */     this.bRender = render;
/*     */     
/*  70 */     if (this.bRender == null)
/*     */     {
/*  72 */       if (Render.class.isAssignableFrom(bWrap.clz())) {
/*  73 */         this.bRender = (Render)bWrap.raw();
/*     */       }
/*     */     }
/*     */     
/*  77 */     if (mapping == null) {
/*  78 */       this.mName = method.getName();
/*  79 */       this.mIsMain = true;
/*     */     } else {
/*  81 */       Produces producesAnno = method.<Produces>getAnnotation(Produces.class);
/*  82 */       Consumes consumesAnno = method.<Consumes>getAnnotation(Consumes.class);
/*     */       
/*  84 */       if (producesAnno == null) {
/*  85 */         this.mProduces = mapping.produces();
/*     */       } else {
/*  87 */         this.mProduces = producesAnno.value();
/*     */       } 
/*     */       
/*  90 */       if (consumesAnno == null) {
/*  91 */         this.mConsumes = mapping.consumes();
/*     */       } else {
/*  93 */         this.mConsumes = consumesAnno.value();
/*     */       } 
/*     */       
/*  96 */       this.mMultipart = mapping.multipart();
/*  97 */       this.mName = Utils.annoAlias(mapping.value(), mapping.path());
/*  98 */       this.mIsMain = (!mapping.after() && !mapping.before());
/*     */     } 
/*     */     
/* 101 */     if (Utils.isEmpty(path)) {
/* 102 */       this.mFullName = this.mName;
/*     */     }
/* 104 */     else if (path.startsWith("/")) {
/* 105 */       this.mFullName = path.substring(1);
/*     */     } else {
/* 107 */       this.mFullName = path;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 112 */     if (!this.mMultipart) {
/* 113 */       for (Class<?> clz : method.getParameterTypes()) {
/* 114 */         if (UploadedFile.class.isAssignableFrom(clz)) {
/* 115 */           this.mMultipart = true;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 121 */     if (path != null && path.contains("{")) {
/* 122 */       this.pathKeys = new ArrayList<>();
/* 123 */       Matcher pm = PathUtil.pathKeyExpr.matcher(path);
/* 124 */       while (pm.find()) {
/* 125 */         this.pathKeys.add(pm.group(1));
/*     */       }
/*     */       
/* 128 */       if (this.pathKeys.size() > 0) {
/* 129 */         this.pathAnalyzer = PathAnalyzer.get(path);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/* 138 */     return this.mName;
/*     */   }
/*     */   
/*     */   public String fullName() {
/* 142 */     return this.mFullName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mapping mapping() {
/* 149 */     return this.mMapping;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodWrap method() {
/* 156 */     return this.mWrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanWrap controller() {
/* 163 */     return this.bWrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String produces() {
/* 170 */     return this.mProduces;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String consumes() {
/* 177 */     return this.mConsumes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(Context x) throws Throwable {
/* 183 */     if (Utils.isNotEmpty(this.mConsumes) && (
/* 184 */       x.contentType() == null || !x.contentType().contains(this.mConsumes))) {
/* 185 */       x.status(415);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 190 */     if (this.mMultipart) {
/* 191 */       x.autoMultipart(true);
/*     */     }
/*     */     
/* 194 */     invoke(x, (Object)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void invoke(Context c, Object obj) throws Throwable {
/* 201 */     c.remotingSet(this.mRemoting);
/*     */ 
/*     */     
/*     */     try {
/* 205 */       if (obj == null) {
/* 206 */         obj = this.bWrap.get();
/*     */       }
/*     */       
/* 209 */       if (this.mIsMain) {
/*     */         
/* 211 */         c.attrSet("controller", obj);
/* 212 */         c.attrSet("action", this);
/*     */       } 
/*     */       
/* 215 */       invoke0(c, obj);
/* 216 */     } catch (Throwable e) {
/* 217 */       c.setHandled(true);
/*     */       
/* 219 */       e = Utils.throwableUnwrap(e);
/*     */       
/* 221 */       if (e instanceof DataThrowable) {
/* 222 */         DataThrowable ex = (DataThrowable)e;
/*     */         
/* 224 */         if (ex.data() == null) {
/* 225 */           renderDo(ex, c);
/*     */         } else {
/* 227 */           renderDo(ex.data(), c);
/*     */         } 
/*     */       } else {
/* 230 */         c.errors = e;
/*     */ 
/*     */         
/* 233 */         EventBus.push(e);
/*     */ 
/*     */         
/* 236 */         if (c.result == null) {
/* 237 */           renderDo(e, c);
/*     */         } else {
/* 239 */           renderDo(c.result, c);
/*     */         } 
/*     */       } 
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
/*     */   protected void invoke0(Context c, Object obj) throws Throwable {
/*     */     try {
/* 259 */       if (this.mIsMain) {
/* 260 */         if (this.bAide != null) {
/* 261 */           for (Handler h : this.bAide.befores) {
/* 262 */             h.handle(c);
/*     */           }
/*     */         }
/*     */         
/* 266 */         for (Handler h : this.befores) {
/* 267 */           h.handle(c);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 273 */       if (!this.mIsMain || !c.getHandled()) {
/*     */ 
/*     */         
/* 276 */         if (this.pathAnalyzer != null) {
/* 277 */           Matcher pm = this.pathAnalyzer.matcher(c.pathNew());
/* 278 */           if (pm.find()) {
/* 279 */             for (int i = 0, len = this.pathKeys.size(); i < len; i++) {
/* 280 */               c.paramSet(this.pathKeys.get(i), pm.group(i + 1));
/*     */             }
/*     */           }
/*     */         } 
/*     */         
/* 285 */         Object tmp = callDo(c, obj, this.mWrap);
/*     */ 
/*     */         
/* 288 */         if (this.mIsMain) {
/*     */ 
/*     */           
/* 291 */           c.result = tmp;
/*     */ 
/*     */           
/* 294 */           if (!Utils.isEmpty(this.mProduces)) {
/* 295 */             c.contentType(this.mProduces);
/*     */           }
/*     */           
/* 298 */           renderDo(tmp, c);
/*     */         } 
/*     */       } 
/* 301 */     } catch (Throwable e) {
/* 302 */       e = Utils.throwableUnwrap(e);
/*     */       
/* 304 */       if (e instanceof DataThrowable) {
/* 305 */         DataThrowable ex = (DataThrowable)e;
/* 306 */         if (ex.data() == null) {
/* 307 */           renderDo(ex, c);
/*     */         } else {
/* 309 */           renderDo(ex.data(), c);
/*     */         } 
/*     */       } else {
/* 312 */         c.errors = e;
/* 313 */         throw e;
/*     */       } 
/*     */     } finally {
/*     */       
/* 317 */       if (this.mIsMain) {
/* 318 */         if (this.bAide != null) {
/* 319 */           for (Handler h : this.bAide.afters) {
/* 320 */             h.handle(c);
/*     */           }
/*     */         }
/*     */         
/* 324 */         for (Handler h : this.afters) {
/* 325 */           h.handle(c);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object callDo(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
/* 335 */     String ct = ctx.contentType();
/*     */     
/* 337 */     if ((mWrap.getParamWraps()).length > 0)
/*     */     {
/*     */ 
/*     */       
/* 341 */       for (ActionExecutor me : Bridge.actionExecutors()) {
/* 342 */         if (me.matched(ctx, ct)) {
/* 343 */           return me.execute(ctx, obj, mWrap);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 348 */     return Bridge.actionExecutorDef().execute(ctx, obj, mWrap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void renderDo(Object obj, Context c) throws Throwable {
/* 358 */     if (c.getRendered()) {
/*     */       return;
/*     */     }
/*     */     
/* 362 */     c.result = obj;
/*     */     
/* 364 */     if (this.bRender == null) {
/*     */       
/* 366 */       if (obj instanceof DataThrowable) {
/*     */         return;
/*     */       }
/*     */       
/* 370 */       if (obj instanceof Throwable) {
/* 371 */         if (c.remoting()) {
/* 372 */           c.render(obj);
/*     */         } else {
/* 374 */           c.setHandled(false);
/* 375 */           throw (Throwable)obj;
/*     */         } 
/*     */       } else {
/* 378 */         c.render(obj);
/*     */       } 
/*     */     } else {
/* 381 */       this.bRender.render(obj, c);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Action.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */