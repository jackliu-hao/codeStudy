/*    */ package org.wildfly.common.selector;
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
/*    */ class ContextClassLoaderSelector
/*    */   extends Selector<ClassLoader>
/*    */ {
/*    */   public ClassLoader get() {
/* 28 */     return Thread.currentThread().getContextClassLoader();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\ContextClassLoaderSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */