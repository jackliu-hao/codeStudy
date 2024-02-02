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
/*    */ public final class GlobalSelector<T>
/*    */   extends Selector<T>
/*    */ {
/*    */   private final T instance;
/*    */   
/*    */   public GlobalSelector(T instance) {
/* 41 */     Assert.checkNotNullParam("instance", instance);
/* 42 */     this.instance = instance;
/*    */   }
/*    */   
/*    */   public T get() {
/* 46 */     return this.instance;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\GlobalSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */