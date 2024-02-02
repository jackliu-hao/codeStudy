/*    */ package org.h2.util;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.h2.engine.SysProperties;
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
/*    */ public final class MemoryUnmapper
/*    */ {
/*    */   private static final boolean ENABLED;
/*    */   private static final Object UNSAFE;
/*    */   private static final Method INVOKE_CLEANER;
/*    */   
/*    */   static {
/* 28 */     boolean bool = SysProperties.NIO_CLEANER_HACK;
/* 29 */     Object object = null;
/* 30 */     Method method = null;
/* 31 */     if (bool) {
/*    */       try {
/* 33 */         Class<?> clazz = Class.forName("sun.misc.Unsafe");
/* 34 */         Field field = clazz.getDeclaredField("theUnsafe");
/* 35 */         field.setAccessible(true);
/* 36 */         object = field.get(null);
/*    */         
/* 38 */         method = clazz.getMethod("invokeCleaner", new Class[] { ByteBuffer.class });
/* 39 */       } catch (ReflectiveOperationException reflectiveOperationException) {
/*    */         
/* 41 */         object = null;
/*    */       }
/* 43 */       catch (Throwable throwable) {
/*    */ 
/*    */         
/* 46 */         bool = false;
/* 47 */         object = null;
/*    */       } 
/*    */     }
/*    */     
/* 51 */     ENABLED = bool;
/* 52 */     UNSAFE = object;
/* 53 */     INVOKE_CLEANER = method;
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
/*    */   public static boolean unmap(ByteBuffer paramByteBuffer) {
/* 66 */     if (!ENABLED) {
/* 67 */       return false;
/*    */     }
/*    */     try {
/* 70 */       if (INVOKE_CLEANER != null) {
/*    */         
/* 72 */         INVOKE_CLEANER.invoke(UNSAFE, new Object[] { paramByteBuffer });
/* 73 */         return true;
/*    */       } 
/*    */       
/* 76 */       Method method = paramByteBuffer.getClass().getMethod("cleaner", new Class[0]);
/* 77 */       method.setAccessible(true);
/* 78 */       Object object = method.invoke(paramByteBuffer, new Object[0]);
/* 79 */       if (object != null) {
/* 80 */         Method method1 = object.getClass().getMethod("clean", new Class[0]);
/* 81 */         method1.invoke(object, new Object[0]);
/*    */       } 
/* 83 */       return true;
/* 84 */     } catch (Throwable throwable) {
/* 85 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\MemoryUnmapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */