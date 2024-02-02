/*    */ package cn.hutool.core.lang.hash;
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
/*    */ public interface Hash128<T>
/*    */   extends Hash<T>
/*    */ {
/*    */   Number128 hash128(T paramT);
/*    */   
/*    */   default Number hash(T t) {
/* 23 */     return hash128(t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\Hash128.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */