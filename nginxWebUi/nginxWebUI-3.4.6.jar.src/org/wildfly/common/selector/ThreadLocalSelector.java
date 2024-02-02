/*    */ package org.wildfly.common.selector;
/*    */ 
/*    */ import org.wildfly.common.Assert;
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
/*    */ @Deprecated
/*    */ public final class ThreadLocalSelector<T>
/*    */   extends Selector<T>
/*    */ {
/*    */   private final ThreadLocal<? extends T> threadLocal;
/*    */   
/*    */   public ThreadLocalSelector(ThreadLocal<? extends T> threadLocal) {
/* 41 */     Assert.checkNotNullParam("threadLocal", threadLocal);
/* 42 */     this.threadLocal = threadLocal;
/*    */   }
/*    */   
/*    */   public T get() {
/* 46 */     return this.threadLocal.get();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\ThreadLocalSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */