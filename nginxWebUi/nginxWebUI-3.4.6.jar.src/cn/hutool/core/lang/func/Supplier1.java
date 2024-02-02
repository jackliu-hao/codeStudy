/*    */ package cn.hutool.core.lang.func;
/*    */ 
/*    */ import java.util.function.Supplier;
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
/*    */ public interface Supplier1<T, P1>
/*    */ {
/*    */   T get(P1 paramP1);
/*    */   
/*    */   default Supplier<T> toSupplier(P1 p1) {
/* 30 */     return () -> get((P1)p1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\Supplier1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */