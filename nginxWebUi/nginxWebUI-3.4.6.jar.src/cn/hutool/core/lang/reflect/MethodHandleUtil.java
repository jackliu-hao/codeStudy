/*     */ package cn.hutool.core.lang.reflect;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.MethodType;
/*     */ import java.lang.reflect.Method;
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
/*     */ public class MethodHandleUtil
/*     */ {
/*     */   public static MethodHandles.Lookup lookup(Class<?> callerClass) {
/*  34 */     return LookupFactory.lookup(callerClass);
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
/*     */   public static MethodHandle findMethod(Class<?> callerClass, String name, MethodType type) {
/*  52 */     if (StrUtil.isBlank(name)) {
/*  53 */       return findConstructor(callerClass, type);
/*     */     }
/*     */     
/*  56 */     MethodHandle handle = null;
/*  57 */     MethodHandles.Lookup lookup = lookup(callerClass);
/*     */     try {
/*  59 */       handle = lookup.findVirtual(callerClass, name, type);
/*  60 */     } catch (IllegalAccessException|NoSuchMethodException illegalAccessException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  65 */     if (null == handle) {
/*     */       try {
/*  67 */         handle = lookup.findStatic(callerClass, name, type);
/*  68 */       } catch (IllegalAccessException|NoSuchMethodException illegalAccessException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     if (null == handle) {
/*     */       try {
/*  76 */         handle = lookup.findSpecial(callerClass, name, type, callerClass);
/*  77 */       } catch (NoSuchMethodException noSuchMethodException) {
/*     */       
/*  79 */       } catch (IllegalAccessException e) {
/*  80 */         throw new UtilException(e);
/*     */       } 
/*     */     }
/*     */     
/*  84 */     return handle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MethodHandle findConstructor(Class<?> callerClass, Class<?>... args) {
/*  95 */     return findConstructor(callerClass, MethodType.methodType(void.class, args));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MethodHandle findConstructor(Class<?> callerClass, MethodType type) {
/* 106 */     MethodHandles.Lookup lookup = lookup(callerClass);
/*     */     try {
/* 108 */       return lookup.findConstructor(callerClass, type);
/* 109 */     } catch (NoSuchMethodException e) {
/* 110 */       return null;
/* 111 */     } catch (IllegalAccessException e) {
/* 112 */       throw new UtilException(e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T invokeSpecial(Object obj, String methodName, Object... args) {
/* 139 */     Assert.notNull(obj, "Object to get method must be not null!", new Object[0]);
/* 140 */     Assert.notBlank(methodName, "Method name must be not blank!", new Object[0]);
/*     */     
/* 142 */     Method method = ReflectUtil.getMethodOfObj(obj, methodName, args);
/* 143 */     if (null == method) {
/* 144 */       throw new UtilException("No such method: [{}] from [{}]", new Object[] { methodName, obj.getClass() });
/*     */     }
/* 146 */     return invokeSpecial(obj, method, args);
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
/*     */   public static <T> T invoke(Object obj, Method method, Object... args) {
/* 159 */     return invoke(false, obj, method, args);
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
/*     */   public static <T> T invokeSpecial(Object obj, Method method, Object... args) {
/* 185 */     return invoke(true, obj, method, args);
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
/*     */ 
/*     */   
/*     */   public static <T> T invoke(boolean isSpecial, Object obj, Method method, Object... args) {
/* 213 */     Assert.notNull(method, "Method must be not null!", new Object[0]);
/* 214 */     Class<?> declaringClass = method.getDeclaringClass();
/* 215 */     MethodHandles.Lookup lookup = lookup(declaringClass);
/*     */     
/*     */     try {
/* 218 */       MethodHandle handle = isSpecial ? lookup.unreflectSpecial(method, declaringClass) : lookup.unreflect(method);
/* 219 */       if (null != obj) {
/* 220 */         handle = handle.bindTo(obj);
/*     */       }
/* 222 */       return (T)handle.invokeWithArguments(args);
/* 223 */     } catch (Throwable e) {
/* 224 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\reflect\MethodHandleUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */