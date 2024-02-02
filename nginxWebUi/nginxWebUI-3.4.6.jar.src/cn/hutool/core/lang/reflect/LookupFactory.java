/*    */ package cn.hutool.core.lang.reflect;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import java.lang.invoke.MethodHandles;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LookupFactory
/*    */ {
/*    */   private static final int ALLOWED_MODES = 15;
/*    */   private static Constructor<MethodHandles.Lookup> java8LookupConstructor;
/*    */   private static Method privateLookupInMethod;
/*    */   
/*    */   static {
/*    */     try {
/* 34 */       privateLookupInMethod = MethodHandles.class.getMethod("privateLookupIn", new Class[] { Class.class, MethodHandles.Lookup.class });
/* 35 */     } catch (NoSuchMethodException noSuchMethodException) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     if (privateLookupInMethod == null) {
/*    */       try {
/* 43 */         java8LookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(new Class[] { Class.class, int.class });
/* 44 */         java8LookupConstructor.setAccessible(true);
/* 45 */       } catch (NoSuchMethodException e) {
/*    */         
/* 47 */         throw new IllegalStateException("There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", e);
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static MethodHandles.Lookup lookup(Class<?> callerClass) {
/* 62 */     if (privateLookupInMethod != null) {
/*    */       try {
/* 64 */         return (MethodHandles.Lookup)privateLookupInMethod.invoke(MethodHandles.class, new Object[] { callerClass, MethodHandles.lookup() });
/* 65 */       } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 66 */         throw new UtilException(e);
/*    */       } 
/*    */     }
/*    */     
/*    */     try {
/* 71 */       return java8LookupConstructor.newInstance(new Object[] { callerClass, Integer.valueOf(15) });
/* 72 */     } catch (Exception e) {
/* 73 */       throw new IllegalStateException("no 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\reflect\LookupFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */