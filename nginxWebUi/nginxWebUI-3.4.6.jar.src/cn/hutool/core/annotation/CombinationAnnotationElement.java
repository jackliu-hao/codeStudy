/*     */ package cn.hutool.core.annotation;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.map.TableMap;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.Documented;
/*     */ import java.lang.annotation.Inherited;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.Target;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
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
/*     */ public class CombinationAnnotationElement
/*     */   implements AnnotatedElement, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static CombinationAnnotationElement of(AnnotatedElement element, Predicate<Annotation> predicate) {
/*  39 */     return new CombinationAnnotationElement(element, predicate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   private static final Set<Class<? extends Annotation>> META_ANNOTATIONS = CollUtil.newHashSet((Object[])new Class[] { Target.class, Retention.class, Inherited.class, Documented.class, SuppressWarnings.class, Override.class, Deprecated.class });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<Class<? extends Annotation>, Annotation> annotationMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<Class<? extends Annotation>, Annotation> declaredAnnotationMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Predicate<Annotation> predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CombinationAnnotationElement(AnnotatedElement element) {
/*  73 */     this(element, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CombinationAnnotationElement(AnnotatedElement element, Predicate<Annotation> predicate) {
/*  84 */     this.predicate = predicate;
/*  85 */     init(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
/*  90 */     return this.annotationMap.containsKey(annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/*  96 */     Annotation annotation = this.annotationMap.get(annotationClass);
/*  97 */     return (annotation == null) ? null : (T)annotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public Annotation[] getAnnotations() {
/* 102 */     Collection<Annotation> annotations = this.annotationMap.values();
/* 103 */     return annotations.<Annotation>toArray(new Annotation[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Annotation[] getDeclaredAnnotations() {
/* 108 */     Collection<Annotation> annotations = this.declaredAnnotationMap.values();
/* 109 */     return annotations.<Annotation>toArray(new Annotation[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(AnnotatedElement element) {
/* 118 */     Annotation[] declaredAnnotations = element.getDeclaredAnnotations();
/* 119 */     this.declaredAnnotationMap = (Map<Class<? extends Annotation>, Annotation>)new TableMap();
/* 120 */     parseDeclared(declaredAnnotations);
/*     */     
/* 122 */     Annotation[] annotations = element.getAnnotations();
/* 123 */     if (Arrays.equals((Object[])declaredAnnotations, (Object[])annotations)) {
/* 124 */       this.annotationMap = this.declaredAnnotationMap;
/*     */     } else {
/* 126 */       this.annotationMap = (Map<Class<? extends Annotation>, Annotation>)new TableMap();
/* 127 */       parse(annotations);
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
/*     */   private void parseDeclared(Annotation[] annotations) {
/* 139 */     for (Annotation annotation : annotations) {
/* 140 */       Class<? extends Annotation> annotationType = annotation.annotationType();
/* 141 */       if (false == META_ANNOTATIONS.contains(annotationType)) {
/* 142 */         if (test(annotation)) {
/* 143 */           this.declaredAnnotationMap.put(annotationType, annotation);
/*     */         }
/*     */         
/* 146 */         parseDeclared(annotationType.getDeclaredAnnotations());
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
/*     */   private void parse(Annotation[] annotations) {
/* 158 */     for (Annotation annotation : annotations) {
/* 159 */       Class<? extends Annotation> annotationType = annotation.annotationType();
/* 160 */       if (false == META_ANNOTATIONS.contains(annotationType)) {
/* 161 */         if (test(annotation)) {
/* 162 */           this.annotationMap.put(annotationType, annotation);
/*     */         }
/*     */         
/* 165 */         parse(annotationType.getAnnotations());
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
/*     */   private boolean test(Annotation annotation) {
/* 177 */     return (null == this.predicate || this.predicate.test(annotation));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\annotation\CombinationAnnotationElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */