/*     */ package org.noear.solon.core.wrap;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Parameter;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.annotation.Around;
/*     */ import org.noear.solon.core.AopContext;
/*     */ import org.noear.solon.core.aspect.Interceptor;
/*     */ import org.noear.solon.core.aspect.InterceptorEntity;
/*     */ import org.noear.solon.core.aspect.Invocation;
/*     */ 
/*     */ public class MethodWrap
/*     */   implements Interceptor, MethodHolder {
/*     */   private final AopContext context;
/*     */   private final Class<?> entityClz;
/*     */   private final Method method;
/*     */   
/*     */   public MethodWrap(AopContext ctx, Method m) {
/*  27 */     this.context = ctx;
/*     */     
/*  29 */     this.entityClz = m.getDeclaringClass();
/*     */     
/*  31 */     this.method = m;
/*  32 */     this.parameters = paramsWrap(m.getParameters());
/*  33 */     this.annotations = m.getAnnotations();
/*  34 */     this.arounds = new ArrayList<>();
/*  35 */     this.aroundsIdx = new HashSet<>();
/*     */ 
/*     */     
/*  38 */     for (Annotation anno : this.annotations) {
/*  39 */       if (anno instanceof Around) {
/*  40 */         doAroundAdd((Around)anno);
/*     */       } else {
/*  42 */         InterceptorEntity ie = this.context.beanAroundGet(anno.annotationType());
/*  43 */         if (ie != null) {
/*  44 */           doAroundAdd(ie);
/*     */         } else {
/*  46 */           doAroundAdd(anno.annotationType().<Around>getAnnotation(Around.class));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  52 */     for (Annotation anno : this.entityClz.getAnnotations()) {
/*  53 */       if (anno instanceof Around) {
/*  54 */         doAroundAdd((Around)anno);
/*     */       } else {
/*  56 */         InterceptorEntity ie = this.context.beanAroundGet(anno.annotationType());
/*  57 */         if (ie != null) {
/*  58 */           doAroundAdd(ie);
/*     */         } else {
/*  60 */           doAroundAdd(anno.annotationType().<Around>getAnnotation(Around.class));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  65 */     if (this.arounds.size() > 0)
/*     */     {
/*  67 */       this.arounds.sort(Comparator.comparing(x -> Integer.valueOf(x.getIndex())));
/*     */     }
/*     */     
/*  70 */     this.arounds.add(new InterceptorEntity(0, this));
/*     */   }
/*     */   private final ParamWrap[] parameters; private final Annotation[] annotations; private final List<InterceptorEntity> arounds; private final Set<Interceptor> aroundsIdx;
/*     */   private ParamWrap[] paramsWrap(Parameter[] pAry) {
/*  74 */     ParamWrap[] tmp = new ParamWrap[pAry.length];
/*  75 */     for (int i = 0, len = pAry.length; i < len; i++) {
/*  76 */       tmp[i] = new ParamWrap(pAry[i]);
/*     */     }
/*     */     
/*  79 */     return tmp;
/*     */   }
/*     */ 
/*     */   
/*     */   private void doAroundAdd(Around a) {
/*  84 */     if (a != null) {
/*  85 */       doAroundAdd(new InterceptorEntity(a.index(), (Interceptor)this.context.wrapAndPut(a.value()).get()));
/*     */     }
/*     */   }
/*     */   
/*     */   private void doAroundAdd(InterceptorEntity i) {
/*  90 */     if (i != null) {
/*  91 */       if (this.aroundsIdx.contains(i.getReal())) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*  96 */       this.aroundsIdx.add(i.getReal());
/*  97 */       this.arounds.add(i);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 120 */     return this.method.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getMethod() {
/* 127 */     return this.method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getReturnType() {
/* 134 */     return this.method.getReturnType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getGenericReturnType() {
/* 141 */     return this.method.getGenericReturnType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParamWrap[] getParamWraps() {
/* 148 */     return this.parameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Annotation[] getAnnotations() {
/* 155 */     return this.annotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Annotation> T getAnnotation(Class<T> type) {
/* 162 */     return this.method.getAnnotation(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
/* 170 */     return this.method.isAnnotationPresent(annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<InterceptorEntity> getArounds() {
/* 177 */     return Collections.unmodifiableList(this.arounds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object doIntercept(Invocation inv) throws Exception {
/* 185 */     return invoke(inv.target(), inv.args());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Object obj, Object[] args) throws Exception {
/*     */     try {
/* 193 */       return this.method.invoke(obj, args);
/* 194 */     } catch (InvocationTargetException e) {
/* 195 */       Throwable ex = e.getTargetException();
/* 196 */       if (ex instanceof Error) {
/* 197 */         throw (Error)ex;
/*     */       }
/* 199 */       throw (Exception)ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
/* 208 */     Invocation inv = new Invocation(obj, args, this, this.arounds);
/* 209 */     return inv.invoke();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\MethodWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */