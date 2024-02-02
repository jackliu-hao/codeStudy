/*     */ package cn.hutool.core.lang.func;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LambdaUtil
/*     */ {
/*  21 */   private static final WeakConcurrentMap<String, SerializedLambda> cache = new WeakConcurrentMap();
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
/*     */   public static <R> Class<R> getRealClass(Func0<?> func) {
/*  58 */     SerializedLambda lambda = resolve(func);
/*  59 */     checkLambdaTypeCanGetClass(lambda.getImplMethodKind());
/*  60 */     return ClassUtil.loadClass(lambda.getImplClass());
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
/*     */   public static <T> SerializedLambda resolve(Func1<T, ?> func) {
/*  72 */     return _resolve(func);
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
/*     */   public static <R> SerializedLambda resolve(Func0<R> func) {
/*  85 */     return _resolve(func);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <P> String getMethodName(Func1<P, ?> func) {
/*  96 */     return resolve(func).getImplMethodName();
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
/*     */   public static <R> String getMethodName(Func0<R> func) {
/* 108 */     return resolve(func).getImplMethodName();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <P, R> Class<P> getRealClass(Func1<P, R> func) {
/* 134 */     SerializedLambda lambda = resolve(func);
/* 135 */     checkLambdaTypeCanGetClass(lambda.getImplMethodKind());
/* 136 */     String instantiatedMethodType = lambda.getInstantiatedMethodType();
/* 137 */     return ClassUtil.loadClass(StrUtil.sub(instantiatedMethodType, 2, StrUtil.indexOf(instantiatedMethodType, ';')));
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
/*     */   public static <T> String getFieldName(Func1<T, ?> func) throws IllegalArgumentException {
/* 156 */     return BeanUtil.getFieldName(getMethodName(func));
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
/*     */   public static <T> String getFieldName(Func0<T> func) throws IllegalArgumentException {
/* 175 */     return BeanUtil.getFieldName(getMethodName(func));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void checkLambdaTypeCanGetClass(int implMethodKind) {
/* 186 */     if (implMethodKind != 5 && implMethodKind != 6)
/*     */     {
/* 188 */       throw new IllegalArgumentException("该lambda不是合适的方法引用");
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
/*     */   private static SerializedLambda _resolve(Serializable func) {
/* 205 */     return (SerializedLambda)cache.computeIfAbsent(func.getClass().getName(), key -> (SerializedLambda)ReflectUtil.invoke(func, "writeReplace", new Object[0]));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\LambdaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */