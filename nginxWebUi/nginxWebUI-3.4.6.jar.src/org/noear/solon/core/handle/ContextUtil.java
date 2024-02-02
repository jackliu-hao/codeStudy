/*    */ package org.noear.solon.core.handle;
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
/*    */ public class ContextUtil
/*    */ {
/*    */   public static final String contentTypeDef = "text/plain;charset=UTF-8";
/* 17 */   private static final ThreadLocal<Context> threadLocal = new InheritableThreadLocal<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void currentSet(Context context) {
/* 23 */     threadLocal.set(context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void currentRemove() {
/* 30 */     threadLocal.remove();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Context current() {
/* 37 */     return threadLocal.get();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\ContextUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */