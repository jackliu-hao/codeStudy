/*    */ package cn.hutool.core.util;
/*    */ 
/*    */ import java.lang.ref.PhantomReference;
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.SoftReference;
/*    */ import java.lang.ref.WeakReference;
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
/*    */ public class ReferenceUtil
/*    */ {
/*    */   public static <T> Reference<T> create(ReferenceType type, T referent) {
/* 32 */     return create(type, referent, null);
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
/*    */   public static <T> Reference<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
/* 45 */     switch (type) {
/*    */       case SOFT:
/* 47 */         return new SoftReference<>(referent, queue);
/*    */       case WEAK:
/* 49 */         return new WeakReference<>(referent, queue);
/*    */       case PHANTOM:
/* 51 */         return new PhantomReference<>(referent, queue);
/*    */     } 
/* 53 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum ReferenceType
/*    */   {
/* 65 */     SOFT,
/*    */     
/* 67 */     WEAK,
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 72 */     PHANTOM;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ReferenceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */