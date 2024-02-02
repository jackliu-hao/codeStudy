/*    */ package io.undertow.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.AbstractCollection;
/*    */ import java.util.Deque;
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
/*    */ public abstract class ConcurrentDirectDeque<E>
/*    */   extends AbstractCollection<E>
/*    */   implements Deque<E>, Serializable
/*    */ {
/*    */   private static final Constructor<? extends ConcurrentDirectDeque> CONSTRUCTOR;
/*    */   
/*    */   static {
/* 34 */     boolean fast = false;
/*    */     try {
/* 36 */       new FastConcurrentDirectDeque();
/* 37 */       fast = true;
/* 38 */     } catch (Throwable throwable) {}
/*    */ 
/*    */     
/* 41 */     Class<? extends ConcurrentDirectDeque> klazz = fast ? (Class)FastConcurrentDirectDeque.class : (Class)PortableConcurrentDirectDeque.class;
/*    */     try {
/* 43 */       CONSTRUCTOR = klazz.getConstructor(new Class[0]);
/* 44 */     } catch (NoSuchMethodException e) {
/* 45 */       throw new NoSuchMethodError(e.getMessage());
/*    */     } 
/*    */   }
/*    */   
/*    */   public static <K> ConcurrentDirectDeque<K> newInstance() {
/*    */     try {
/* 51 */       return CONSTRUCTOR.newInstance(new Object[0]);
/* 52 */     } catch (Exception e) {
/* 53 */       throw new IllegalStateException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public abstract void removeToken(Object paramObject);
/*    */   
/*    */   public abstract Object offerLastAndReturnToken(E paramE);
/*    */   
/*    */   public abstract Object offerFirstAndReturnToken(E paramE);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ConcurrentDirectDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */