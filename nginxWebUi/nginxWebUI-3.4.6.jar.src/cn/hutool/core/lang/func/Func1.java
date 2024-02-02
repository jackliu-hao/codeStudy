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
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface Func1<P, R>
/*    */   extends Serializable
/*    */ {
/*    */   R call(P paramP) throws Exception;
/*    */   
/*    */   default R callWithRuntimeException(P parameter) {
/*    */     try {
/* 38 */       return call(parameter);
/* 39 */     } catch (Exception e) {
/* 40 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\Func1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */