/*    */ package cn.hutool.core.lang.loader;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.concurrent.atomic.AtomicReference;
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
/*    */ public abstract class AtomicLoader<T>
/*    */   implements Loader<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 24 */   private final AtomicReference<T> reference = new AtomicReference<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T get() {
/* 31 */     T result = this.reference.get();
/*    */     
/* 33 */     if (result == null) {
/* 34 */       result = init();
/* 35 */       if (false == this.reference.compareAndSet(null, result))
/*    */       {
/* 37 */         result = this.reference.get();
/*    */       }
/*    */     } 
/*    */     
/* 41 */     return result;
/*    */   }
/*    */   
/*    */   protected abstract T init();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\loader\AtomicLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */