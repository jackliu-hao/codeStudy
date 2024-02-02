/*     */ package cn.hutool.core.annotation;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.Documented;
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Inherited;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class AnnotationUtil
/*     */ {
/*     */   public static CombinationAnnotationElement toCombination(AnnotatedElement annotationEle) {
/*  37 */     if (annotationEle instanceof CombinationAnnotationElement) {
/*  38 */       return (CombinationAnnotationElement)annotationEle;
/*     */     }
/*  40 */     return new CombinationAnnotationElement(annotationEle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination) {
/*  51 */     return getAnnotations(annotationEle, isToCombination, (Predicate<Annotation>)null);
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
/*     */   public static <T> T[] getCombinationAnnotations(AnnotatedElement annotationEle, Class<T> annotationType) {
/*  64 */     return getAnnotations(annotationEle, true, annotationType);
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
/*     */   public static <T> T[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination, Class<T> annotationType) {
/*  78 */     Annotation[] annotations = getAnnotations(annotationEle, isToCombination, annotation -> 
/*  79 */         (null == annotationType || annotationType.isAssignableFrom(annotation.getClass())));
/*     */     
/*  81 */     T[] result = (T[])ArrayUtil.newArray(annotationType, annotations.length);
/*  82 */     for (int i = 0; i < annotations.length; i++)
/*     */     {
/*  84 */       result[i] = (T)annotations[i];
/*     */     }
/*  86 */     return result;
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
/*     */   public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination, Predicate<Annotation> predicate) {
/*  99 */     if (null == annotationEle) {
/* 100 */       return null;
/*     */     }
/*     */     
/* 103 */     if (isToCombination) {
/* 104 */       if (null == predicate) {
/* 105 */         return toCombination(annotationEle).getAnnotations();
/*     */       }
/* 107 */       return CombinationAnnotationElement.of(annotationEle, predicate).getAnnotations();
/*     */     } 
/*     */     
/* 110 */     Annotation[] result = annotationEle.getAnnotations();
/* 111 */     if (null == predicate) {
/* 112 */       return result;
/*     */     }
/* 114 */     return (Annotation[])ArrayUtil.filter((Object[])result, predicate::test);
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
/*     */   public static <A extends Annotation> A getAnnotation(AnnotatedElement annotationEle, Class<A> annotationType) {
/* 126 */     return (null == annotationEle) ? null : toCombination(annotationEle).<A>getAnnotation(annotationType);
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
/*     */   public static boolean hasAnnotation(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) {
/* 138 */     return (null != getAnnotation(annotationEle, (Class)annotationType));
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
/*     */   public static <T> T getAnnotationValue(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) throws UtilException {
/* 152 */     return getAnnotationValue(annotationEle, annotationType, "value");
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
/*     */   public static <T> T getAnnotationValue(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType, String propertyName) throws UtilException {
/* 167 */     Annotation annotation = getAnnotation(annotationEle, (Class)annotationType);
/* 168 */     if (null == annotation) {
/* 169 */       return null;
/*     */     }
/*     */     
/* 172 */     Method method = ReflectUtil.getMethodOfObj(annotation, propertyName, new Object[0]);
/* 173 */     if (null == method) {
/* 174 */       return null;
/*     */     }
/* 176 */     return (T)ReflectUtil.invoke(annotation, method, new Object[0]);
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
/*     */   public static Map<String, Object> getAnnotationValueMap(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) throws UtilException {
/* 189 */     Annotation annotation = getAnnotation(annotationEle, (Class)annotationType);
/* 190 */     if (null == annotation) {
/* 191 */       return null;
/*     */     }
/*     */     
/* 194 */     Method[] methods = ReflectUtil.getMethods(annotationType, t -> {
/*     */           if (ArrayUtil.isEmpty((Object[])t.getParameterTypes())) {
/*     */             String name = t.getName();
/*     */ 
/*     */             
/* 199 */             return (false == "hashCode".equals(name) && false == "toString".equals(name) && false == "annotationType".equals(name));
/*     */           } 
/*     */ 
/*     */           
/*     */           return false;
/*     */         });
/*     */     
/* 206 */     HashMap<String, Object> result = new HashMap<>(methods.length, 1.0F);
/* 207 */     for (Method method : methods) {
/* 208 */       result.put(method.getName(), ReflectUtil.invoke(annotation, method, new Object[0]));
/*     */     }
/* 210 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
/* 220 */     Retention retention = annotationType.<Retention>getAnnotation(Retention.class);
/* 221 */     if (null == retention) {
/* 222 */       return RetentionPolicy.CLASS;
/*     */     }
/* 224 */     return retention.value();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
/* 234 */     Target target = annotationType.<Target>getAnnotation(Target.class);
/* 235 */     if (null == target) {
/* 236 */       return new ElementType[] { ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 246 */     return target.value();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isDocumented(Class<? extends Annotation> annotationType) {
/* 256 */     return annotationType.isAnnotationPresent((Class)Documented.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInherited(Class<? extends Annotation> annotationType) {
/* 266 */     return annotationType.isAnnotationPresent((Class)Inherited.class);
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
/*     */   public static void setValue(Annotation annotation, String annotationField, Object value) {
/* 279 */     Map<String, Object> memberValues = (Map)ReflectUtil.getFieldValue(Proxy.getInvocationHandler(annotation), "memberValues");
/* 280 */     memberValues.put(annotationField, value);
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
/*     */   public static <T extends Annotation> T getAnnotationAlias(AnnotatedElement annotationEle, Class<T> annotationType) {
/* 294 */     T annotation = getAnnotation(annotationEle, annotationType);
/* 295 */     return (T)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[] { annotationType }, new AnnotationProxy<>(annotation));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\annotation\AnnotationUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */