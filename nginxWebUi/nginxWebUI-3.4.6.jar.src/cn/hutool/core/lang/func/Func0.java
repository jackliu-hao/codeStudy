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
/*    */ public interface Func0<R>
/*    */   extends Serializable
/*    */ {
/*    */   R call() throws Exception;
/*    */   
/*    */   default R callWithRuntimeException() {
/*    */     try {
/* 34 */       return call();
/* 35 */     } catch (Exception e) {
/* 36 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\Func0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */