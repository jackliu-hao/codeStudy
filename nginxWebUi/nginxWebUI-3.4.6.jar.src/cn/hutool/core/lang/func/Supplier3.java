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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface Supplier3<T, P1, P2, P3>
/*    */ {
/*    */   T get(P1 paramP1, P2 paramP2, P3 paramP3);
/*    */   
/*    */   default Supplier<T> toSupplier(P1 p1, P2 p2, P3 p3) {
/* 37 */     return () -> get((P1)p1, (P2)p2, (P3)p3);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\func\Supplier3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */