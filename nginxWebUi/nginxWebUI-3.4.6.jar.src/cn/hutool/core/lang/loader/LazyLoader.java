/*    */ package cn.hutool.core.lang.loader;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public abstract class LazyLoader<T>
/*    */   implements Loader<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private volatile T object;
/*    */   
/*    */   public T get() {
/* 26 */     T result = this.object;
/* 27 */     if (result == null) {
/* 28 */       synchronized (this) {
/* 29 */         result = this.object;
/* 30 */         if (result == null) {
/* 31 */           this.object = result = init();
/*    */         }
/*    */       } 
/*    */     }
/* 35 */     return result;
/*    */   }
/*    */   
/*    */   protected abstract T init();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\loader\LazyLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */