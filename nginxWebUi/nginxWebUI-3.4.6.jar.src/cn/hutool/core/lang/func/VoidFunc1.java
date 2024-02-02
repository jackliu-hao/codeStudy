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
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface VoidFunc1<P>
/*    */   extends Serializable
/*    */ {
/*    */   void call(P paramP) throws Exception;
/*    */   
/*    */   default void callWithRuntimeException(P parameter) {
/*    */     try {
/* 34 */       call(parameter);
/* 35 */     } catch (Exception e) {
/* 36 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\VoidFunc1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */