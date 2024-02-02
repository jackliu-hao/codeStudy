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
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface VoidFunc<P>
/*    */   extends Serializable
/*    */ {
/*    */   void call(P... paramVarArgs) throws Exception;
/*    */   
/*    */   void callWithRuntimeException(P... parameters) {
/*    */     try {
/* 36 */       call(parameters);
/* 37 */     } catch (Exception e) {
/* 38 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\VoidFunc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */