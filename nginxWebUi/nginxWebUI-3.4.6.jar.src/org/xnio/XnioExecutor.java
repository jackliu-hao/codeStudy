/*    */ package org.xnio;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ public interface XnioExecutor
/*    */   extends Executor
/*    */ {
/*    */   void execute(Runnable paramRunnable);
/*    */   
/*    */   Key executeAfter(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
/*    */   
/*    */   Key executeAtInterval(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
/*    */   
/*    */   public static interface Key
/*    */   {
/* 78 */     public static final Key IMMEDIATE = new Key() {
/*    */         public boolean remove() {
/* 80 */           return false;
/*    */         }
/*    */         
/*    */         public String toString() {
/* 84 */           return "Immediate key";
/*    */         }
/*    */       };
/*    */     
/*    */     boolean remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */