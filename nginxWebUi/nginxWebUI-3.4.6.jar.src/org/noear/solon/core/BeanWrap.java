/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import org.noear.solon.annotation.Init;
/*     */ import org.noear.solon.annotation.Singleton;
/*     */ import org.noear.solon.core.util.IndexBuilder;
/*     */ import org.noear.solon.core.wrap.ClassWrap;
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
/*     */ public class BeanWrap
/*     */ {
/*     */   private Class<?> clz;
/*     */   private Method clzInit;
/*     */   private boolean clzInitDelay;
/*     */   private Object raw;
/*     */   private boolean singleton;
/*     */   private boolean remoting;
/*     */   private String name;
/*     */   private String tag;
/*     */   private String[] attrs;
/*     */   private Map<String, String> attrMap;
/*     */   private boolean typed;
/*     */   private Proxy proxy;
/*     */   private final Annotation[] annotations;
/*     */   private final AopContext context;
/*     */   
/*     */   public BeanWrap(AopContext context, Class<?> clz) {
/*  55 */     this(context, clz, null);
/*     */   }
/*     */   
/*     */   public BeanWrap(AopContext context, Class<?> clz, Object raw) {
/*  59 */     this.context = context;
/*  60 */     this.clz = clz;
/*     */     
/*  62 */     Singleton ano = clz.<Singleton>getAnnotation(Singleton.class);
/*  63 */     this.singleton = (ano == null || ano.value());
/*  64 */     this.annotations = clz.getAnnotations();
/*     */     
/*  66 */     tryBuildInit();
/*     */     
/*  68 */     if (raw == null) {
/*  69 */       this.raw = _new();
/*     */     } else {
/*  71 */       this.raw = raw;
/*     */     } 
/*     */   }
/*     */   
/*     */   public BeanWrap(AopContext context, Class<?> clz, Object raw, String[] attrs) {
/*  76 */     this(context, clz, raw);
/*  77 */     attrsSet(attrs);
/*     */   }
/*     */   
/*     */   public AopContext context() {
/*  81 */     return this.context;
/*     */   }
/*     */   
/*     */   public Proxy proxy() {
/*  85 */     return this.proxy;
/*     */   }
/*     */ 
/*     */   
/*     */   public void proxySet(Proxy proxy) {
/*  90 */     this.proxy = proxy;
/*     */     
/*  92 */     if (this.raw != null)
/*     */     {
/*  94 */       this.raw = proxy.getProxy(context(), this.raw);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean singleton() {
/* 102 */     return this.singleton;
/*     */   }
/*     */   
/*     */   public void singletonSet(boolean singleton) {
/* 106 */     this.singleton = singleton;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remoting() {
/* 113 */     return this.remoting;
/*     */   }
/*     */   
/*     */   public void remotingSet(boolean remoting) {
/* 117 */     this.remoting = remoting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> clz() {
/* 124 */     return this.clz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T raw() {
/* 131 */     return (T)this.raw;
/*     */   }
/*     */   
/*     */   protected void rawSet(Object raw) {
/* 135 */     this.raw = raw;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/* 142 */     return this.name;
/*     */   }
/*     */   
/*     */   protected void nameSet(String name) {
/* 146 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String tag() {
/* 153 */     return this.tag;
/*     */   }
/*     */   
/*     */   protected void tagSet(String tag) {
/* 157 */     this.tag = tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] attrs() {
/* 164 */     return this.attrs;
/*     */   }
/*     */   
/*     */   protected void attrsSet(String[] attrs) {
/* 168 */     this.attrs = attrs;
/*     */   }
/*     */   
/*     */   public String attrGet(String name) {
/* 172 */     if (this.attrs == null) {
/* 173 */       return null;
/*     */     }
/*     */     
/* 176 */     if (this.attrMap == null) {
/* 177 */       this.attrMap = new HashMap<>();
/*     */       
/* 179 */       for (String kv : this.attrs) {
/* 180 */         String[] ss = kv.split("=");
/* 181 */         if (ss.length == 2) {
/* 182 */           this.attrMap.put(ss[0], ss[1]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 187 */     return this.attrMap.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean typed() {
/* 194 */     return this.typed;
/*     */   }
/*     */   
/*     */   protected void typedSet(boolean typed) {
/* 198 */     this.typed = typed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Annotation[] annotations() {
/* 205 */     return this.annotations;
/*     */   }
/*     */   
/*     */   public <T extends Annotation> T annotationGet(Class<T> annClz) {
/* 209 */     return this.clz.getAnnotation(annClz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T get() {
/* 216 */     if (this.singleton) {
/* 217 */       return (T)this.raw;
/*     */     }
/* 219 */     return (T)_new();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init(Object bean) {
/* 228 */     this.context.beanInject(bean);
/*     */ 
/*     */     
/* 231 */     if (this.clzInit != null) {
/* 232 */       if (this.clzInitDelay) {
/* 233 */         this.context.beanOnloaded(IndexBuilder.buildIndex(this.clz), ctx -> initInvokeDo(bean));
/*     */       }
/*     */       else {
/*     */         
/* 237 */         initInvokeDo(bean);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initInvokeDo(Object bean) {
/*     */     try {
/* 245 */       this.clzInit.invoke(bean, new Object[0]);
/* 246 */     } catch (RuntimeException ex) {
/* 247 */       throw ex;
/* 248 */     } catch (ReflectiveOperationException ex) {
/* 249 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _new() {
/* 257 */     if (this.clz.isInterface()) {
/* 258 */       return this.raw;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 263 */       Object bean = this.clz.newInstance();
/*     */ 
/*     */       
/* 266 */       init(bean);
/*     */       
/* 268 */       if (this.proxy != null) {
/* 269 */         bean = this.proxy.getProxy(context(), bean);
/*     */       }
/*     */       
/* 272 */       return bean;
/* 273 */     } catch (RuntimeException ex) {
/* 274 */       throw ex;
/* 275 */     } catch (Throwable ex) {
/* 276 */       throw new IllegalArgumentException("Instantiation failure: " + this.clz.getTypeName(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void tryBuildInit() {
/* 284 */     if (this.clzInit != null) {
/*     */       return;
/*     */     }
/*     */     
/* 288 */     if (this.clz.isInterface()) {
/*     */       return;
/*     */     }
/*     */     
/* 292 */     ClassWrap clzWrap = ClassWrap.get(this.clz);
/*     */ 
/*     */     
/* 295 */     for (Method m : clzWrap.getMethods()) {
/* 296 */       Init initAnno = m.<Init>getAnnotation(Init.class);
/* 297 */       if (initAnno != null) {
/* 298 */         if ((m.getParameters()).length == 0) {
/*     */           
/* 300 */           this.clzInit = m;
/* 301 */           this.clzInit.setAccessible(true);
/* 302 */           this.clzInitDelay = initAnno.delay();
/*     */         } 
/*     */         break;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 325 */     if (this == o) return true; 
/* 326 */     if (!(o instanceof BeanWrap)) return false; 
/* 327 */     BeanWrap beanWrap = (BeanWrap)o;
/* 328 */     return this.clz.equals(beanWrap.clz);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 333 */     return Objects.hash(new Object[] { this.clz });
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface Proxy {
/*     */     Object getProxy(AopContext param1AopContext, Object param1Object);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\BeanWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */