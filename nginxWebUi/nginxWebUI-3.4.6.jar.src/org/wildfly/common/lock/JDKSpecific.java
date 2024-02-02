/*    */ package org.wildfly.common.lock;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import sun.misc.Unsafe;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JDKSpecific
/*    */ {
/* 35 */   static final Unsafe unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*    */         public Unsafe run() {
/*    */           try {
/* 38 */             Field field = Unsafe.class.getDeclaredField("theUnsafe");
/* 39 */             field.setAccessible(true);
/* 40 */             return (Unsafe)field.get(null);
/* 41 */           } catch (IllegalAccessException e) {
/* 42 */             throw new IllegalAccessError(e.getMessage());
/* 43 */           } catch (NoSuchFieldException e) {
/* 44 */             throw new NoSuchFieldError(e.getMessage());
/*    */           } 
/*    */         }
/*    */       });
/*    */   
/*    */   static void onSpinWait() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\JDKSpecific.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */