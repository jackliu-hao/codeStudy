/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiPredicate;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Note;
/*     */ import org.noear.solon.core.aspect.Interceptor;
/*     */ import org.noear.solon.core.aspect.InterceptorEntity;
/*     */ import org.noear.solon.core.handle.HandlerLoader;
/*     */ import org.noear.solon.core.handle.HandlerSlots;
/*     */ import org.noear.solon.core.util.ConvertUtil;
/*     */ 
/*     */ 
/*     */ public abstract class BeanContainer
/*     */ {
/*     */   private final Props props;
/*     */   private final ClassLoader classLoader;
/*  31 */   private Map<Class<?>, Object> attrs = new HashMap<>();
/*     */   
/*     */   private final Map<Class<?>, BeanWrap> beanWraps;
/*     */   
/*     */   private final Set<BeanWrap> beanWrapSet;
/*     */   private final Map<String, BeanWrap> beans;
/*     */   private final Map<Class<?>, Class<?>> clzMapping;
/*     */   
/*     */   public Props getProps() {
/*  40 */     if (this.props == null) {
/*  41 */       return (Props)Solon.cfg();
/*     */     }
/*  43 */     return this.props;
/*     */   }
/*     */   protected final Map<Class<?>, BeanBuilder<?>> beanBuilders; protected final Map<Class<?>, BeanInjector<?>> beanInjectors; protected final Map<Class<?>, BeanExtractor<?>> beanExtractors; protected final Map<Class<?>, InterceptorEntity> beanInterceptors; protected final Set<SubscriberEntity> beanSubscribers;
/*     */   
/*     */   public Map<Class<?>, Object> getAttrs() {
/*  48 */     return this.attrs;
/*     */   }
/*     */   
/*     */   public ClassLoader getClassLoader() {
/*  52 */     if (this.classLoader == null) {
/*  53 */       return JarClassLoader.global();
/*     */     }
/*  55 */     return this.classLoader;
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
/*     */   public BeanContainer(ClassLoader classLoader, Props props) {
/*  67 */     this.beanWraps = new HashMap<>();
/*  68 */     this.beanWrapSet = new HashSet<>();
/*     */ 
/*     */ 
/*     */     
/*  72 */     this.beans = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.clzMapping = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     this.beanBuilders = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*  86 */     this.beanInjectors = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*  90 */     this.beanExtractors = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*  94 */     this.beanInterceptors = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     this.beanSubscribers = new LinkedHashSet<>();
/*     */     this.classLoader = classLoader;
/*     */     this.props = props;
/*     */   } public void clear() {
/* 105 */     this.beanWraps.clear();
/* 106 */     this.beanWrapSet.clear();
/* 107 */     this.beans.clear();
/* 108 */     this.clzMapping.clear();
/* 109 */     this.attrs.clear();
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
/*     */   public void copyTo(BeanContainer container) {
/* 123 */     this.beanBuilders.forEach((k, v) -> container.beanBuilders.putIfAbsent(k, v));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     this.beanInjectors.forEach((k, v) -> container.beanInjectors.putIfAbsent(k, v));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     this.beanInterceptors.forEach((k, v) -> container.beanInterceptors.putIfAbsent(k, v));
/*     */ 
/*     */ 
/*     */     
/* 137 */     this.beanExtractors.forEach((k, v) -> container.beanExtractors.putIfAbsent(k, v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> void beanBuilderAdd(Class<T> anno, BeanBuilder<T> builder) {
/* 146 */     this.beanBuilders.put(anno, builder);
/*     */   }
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
/* 150 */     this.beanInjectors.put(anno, injector);
/*     */   }
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> void beanExtractorAdd(Class<T> anno, BeanExtractor<T> extractor) {
/* 154 */     this.beanExtractors.put(anno, extractor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor, int index) {
/* 163 */     this.beanInterceptors.put(anno, new InterceptorEntity(index, interceptor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor) {
/* 170 */     beanAroundAdd(anno, interceptor, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.lang.annotation.Annotation> InterceptorEntity beanAroundGet(Class<T> anno) {
/* 177 */     return this.beanInterceptors.get(anno);
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
/*     */   public void beanSubscribe(Object nameOrType, Consumer<BeanWrap> callback) {
/* 190 */     if (nameOrType != null) {
/* 191 */       this.beanSubscribers.add(new SubscriberEntity(nameOrType, callback));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beanNotice(Object nameOrType, BeanWrap wrap) {
/* 199 */     if (wrap.raw() == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 204 */     (new ArrayList(this.beanSubscribers)).forEach(s1 -> {
/*     */           if (s1.key.equals(nameOrType)) {
/*     */             s1.callback.accept(wrap);
/*     */           }
/*     */         });
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
/*     */   public synchronized void putWrap(String name, BeanWrap wrap) {
/* 221 */     if (!Utils.isEmpty(name) && wrap.raw() != null && 
/* 222 */       !this.beans.containsKey(name)) {
/* 223 */       this.beans.put(name, wrap);
/* 224 */       beanNotice(name, wrap);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void putWrap(Class<?> type, BeanWrap wrap) {
/* 235 */     if (type != null && wrap.raw() != null)
/*     */     {
/*     */ 
/*     */       
/* 239 */       if (!this.beanWraps.containsKey(type)) {
/* 240 */         this.beanWraps.put(type, wrap);
/* 241 */         this.beanWrapSet.add(wrap);
/* 242 */         beanNotice(type, wrap);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasWrap(Object nameOrType) {
/* 248 */     return (getWrap(nameOrType) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanWrap getWrap(Object nameOrType) {
/* 257 */     if (nameOrType instanceof String) {
/* 258 */       return this.beans.get(nameOrType);
/*     */     }
/* 260 */     return this.beanWraps.get(nameOrType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void getWrapAsyn(Object nameOrType, Consumer<BeanWrap> callback) {
/* 265 */     BeanWrap bw = getWrap(nameOrType);
/*     */     
/* 267 */     if (bw == null || bw.raw() == null) {
/* 268 */       beanSubscribe(nameOrType, callback);
/*     */     } else {
/* 270 */       callback.accept(bw);
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> T getBean(Object nameOrType) {
/* 275 */     BeanWrap bw = getWrap(nameOrType);
/* 276 */     return (bw == null) ? null : bw.<T>get();
/*     */   }
/*     */   
/*     */   public <T> T getBeanOrNew(Class<T> type) {
/* 280 */     return wrapAndPut(type).get();
/*     */   }
/*     */   
/*     */   public <T> void getBeanAsyn(Object nameOrType, Consumer<T> callback) {
/* 284 */     getWrapAsyn(nameOrType, bw -> callback.accept(bw.get()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanWrap wrap(Class<?> type, Object bean) {
/* 293 */     BeanWrap wrap = getWrap(type);
/* 294 */     if (wrap == null) {
/* 295 */       wrap = wrapCreate(type, bean);
/*     */     }
/*     */     
/* 298 */     return wrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanWrap wrapAndPut(Class<?> type) {
/* 305 */     return wrapAndPut(type, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanWrap wrapAndPut(Class<?> type, Object bean) {
/* 312 */     BeanWrap wrap = getWrap(type);
/* 313 */     if (wrap == null) {
/* 314 */       wrap = wrapCreate(type, bean);
/* 315 */       putWrap(type, wrap);
/*     */     } 
/*     */     
/* 318 */     return wrap;
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
/*     */   public void beanRegister(BeanWrap bw, String name, boolean typed) {
/* 334 */     if (Utils.isNotEmpty(name)) {
/*     */ 
/*     */       
/* 337 */       putWrap(name, bw);
/* 338 */       if (!typed) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 345 */     putWrap(bw.clz(), bw);
/* 346 */     putWrap(bw.clz().getName(), bw);
/*     */ 
/*     */     
/* 349 */     beanRegisterSup0(bw);
/*     */ 
/*     */     
/* 352 */     if (bw.remoting()) {
/* 353 */       HandlerLoader bww = new HandlerLoader(bw);
/* 354 */       if (bww.mapping() != null)
/*     */       {
/*     */ 
/*     */         
/* 358 */         bww.load((HandlerSlots)Solon.app());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void beanRegisterSup0(BeanWrap bw) {
/* 368 */     Class<?>[] list = bw.clz().getInterfaces();
/* 369 */     for (Class<?> c : list) {
/* 370 */       if (!c.getName().contains("java.")) {
/*     */         
/* 372 */         this.clzMapping.putIfAbsent(c, bw.clz());
/* 373 */         putWrap(c, bw);
/*     */       } 
/*     */     } 
/*     */     
/* 377 */     Type[] list2 = bw.clz().getGenericInterfaces();
/* 378 */     for (Type t : list2) {
/* 379 */       if (t instanceof java.lang.reflect.ParameterizedType) {
/* 380 */         putWrap(t.getTypeName(), bw);
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
/*     */   public void beanInject(VarHolder varH, String name) {
/* 393 */     beanInject(varH, name, false);
/*     */   }
/*     */   
/*     */   protected void beanInject(VarHolder varH, String name, boolean autoRefreshed) {
/* 397 */     if (Utils.isEmpty(name)) {
/*     */ 
/*     */ 
/*     */       
/* 401 */       if (AopContext.class.isAssignableFrom(varH.getType())) {
/* 402 */         varH.setValue(this);
/*     */         
/*     */         return;
/*     */       } 
/* 406 */       if (varH.getGenericType() != null) {
/* 407 */         getWrapAsyn(varH.getGenericType().getTypeName(), bw -> varH.setValue(bw.get()));
/*     */       }
/*     */       else {
/*     */         
/* 411 */         getWrapAsyn(varH.getType(), bw -> varH.setValue(bw.get()));
/*     */       }
/*     */     
/*     */     }
/* 415 */     else if (name.startsWith("${classpath:")) {
/*     */ 
/*     */ 
/*     */       
/* 419 */       String url = name.substring(12, name.length() - 1);
/* 420 */       Properties val = Utils.loadProperties(Utils.getResource(getClassLoader(), url));
/*     */       
/* 422 */       if (val == null) {
/* 423 */         throw new IllegalStateException(name + "  failed to load!");
/*     */       }
/*     */       
/* 426 */       if (Properties.class == varH.getType()) {
/* 427 */         varH.setValue(val);
/* 428 */       } else if (Map.class == varH.getType()) {
/* 429 */         Map<String, String> val2 = new HashMap<>();
/* 430 */         val.forEach((k, v) -> {
/*     */               if (k instanceof String && v instanceof String) {
/*     */                 val2.put((String)k, (String)v);
/*     */               }
/*     */             });
/* 435 */         varH.setValue(val2);
/*     */       } else {
/* 437 */         Object val2 = PropsConverter.global().convert(val, null, varH.getType(), varH.getGenericType());
/* 438 */         varH.setValue(val2);
/*     */       } 
/* 440 */     } else if (name.startsWith("${")) {
/*     */ 
/*     */ 
/*     */       
/* 444 */       String name2 = name.substring(2, name.length() - 1).trim();
/*     */       
/* 446 */       beanInjectConfig(varH, name2);
/*     */       
/* 448 */       if (autoRefreshed && varH.isField()) {
/* 449 */         getProps().onChange((key, val) -> {
/*     */ 
/*     */               
/*     */               if (key.startsWith(name2)) {
/*     */                 beanInjectConfig(varH, name2);
/*     */               }
/*     */             });
/*     */       }
/*     */     } else {
/*     */       
/* 459 */       getWrapAsyn(name, bw -> {
/*     */             if (BeanWrap.class.isAssignableFrom(varH.getType())) {
/*     */               varH.setValue(bw);
/*     */             } else {
/*     */               varH.setValue(bw.get());
/*     */             } 
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void beanInjectProperties(Class<?> clz, Object obj) {
/* 470 */     Inject typeInj = clz.<Inject>getAnnotation(Inject.class);
/*     */     
/* 472 */     if (typeInj != null && Utils.isNotEmpty(typeInj.value()) && 
/* 473 */       typeInj.value().startsWith("${")) {
/* 474 */       Utils.injectProperties(obj, getProps().getPropByExpr(typeInj.value()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void beanInjectConfig(VarHolder varH, String name) {
/* 480 */     if (Properties.class == varH.getType()) {
/*     */       
/* 482 */       Properties val = getProps().getProp(name);
/* 483 */       varH.setValue(val);
/*     */     } else {
/*     */       
/* 486 */       String def = null;
/* 487 */       int defIdx = name.indexOf(":");
/* 488 */       if (defIdx > 0) {
/* 489 */         if (name.length() > defIdx + 1) {
/* 490 */           def = name.substring(defIdx + 1).trim();
/*     */         } else {
/* 492 */           def = "";
/*     */         } 
/* 494 */         name = name.substring(0, defIdx).trim();
/*     */       } 
/*     */       
/* 497 */       String val = getProps().get(name);
/*     */       
/* 499 */       if (def != null && 
/* 500 */         Utils.isEmpty(val)) {
/* 501 */         val = def;
/*     */       }
/*     */ 
/*     */       
/* 505 */       if (val == null) {
/* 506 */         Class<?> pt = varH.getType();
/*     */         
/* 508 */         if (!pt.getName().startsWith("java.lang.") && !pt.isPrimitive()) {
/*     */ 
/*     */ 
/*     */           
/* 512 */           Properties val0 = getProps().getProp(name);
/* 513 */           if (val0.size() > 0) {
/*     */             
/* 515 */             Object val2 = PropsConverter.global().convert(val0, null, pt, varH.getGenericType());
/* 516 */             varH.setValue(val2);
/*     */           } 
/*     */         } 
/*     */       } else {
/* 520 */         Object val2 = ConvertUtil.to(varH.getType(), val);
/* 521 */         varH.setValue(val2);
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
/*     */   @Note("遍历bean库 (拿到的是bean包装)")
/*     */   public void beanForeach(BiConsumer<String, BeanWrap> action) {
/* 537 */     this.beans.forEach(action);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("遍历bean包装库")
/*     */   public void beanForeach(Consumer<BeanWrap> action) {
/* 546 */     (new ArrayList(this.beanWrapSet)).forEach(bw -> action.accept(bw));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("查找bean库 (拿到的是bean包装)")
/*     */   public List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> condition) {
/* 556 */     List<BeanWrap> list = new ArrayList<>();
/* 557 */     beanForeach((k, v) -> {
/*     */           if (condition.test(k, v)) {
/*     */             list.add(v);
/*     */           }
/*     */         });
/*     */     
/* 563 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("查找bean包装库")
/*     */   public List<BeanWrap> beanFind(Predicate<BeanWrap> condition) {
/* 571 */     List<BeanWrap> list = new ArrayList<>();
/* 572 */     beanForeach(v -> {
/*     */           if (condition.test(v)) {
/*     */             list.add(v);
/*     */           }
/*     */         });
/*     */     
/* 578 */     return list;
/*     */   }
/*     */   
/*     */   protected abstract BeanWrap wrapCreate(Class<?> paramClass, Object paramObject);
/*     */   
/*     */   class SubscriberEntity
/*     */   {
/*     */     public Object key;
/*     */     public Consumer<BeanWrap> callback;
/*     */     
/*     */     public SubscriberEntity(Object key, Consumer<BeanWrap> callback) {
/* 589 */       this.key = key;
/* 590 */       this.callback = callback;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\BeanContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */