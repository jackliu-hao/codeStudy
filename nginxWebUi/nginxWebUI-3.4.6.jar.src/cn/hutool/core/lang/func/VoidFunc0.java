/*    */ package cn.hutool.core.lang.func;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface VoidFunc0
/*    */   extends Serializable
/*    */ {
/*    */   void call() throws Exception;
/*    */   
/*    */   default void callWithRuntimeException() {
/*    */     try {
/* 32 */       call();
/* 33 */     } catch (Exception e) {
/* 34 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\VoidFunc0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */