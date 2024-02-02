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
/*    */ @FunctionalInterface
/*    */ public interface Hash32<T>
/*    */   extends Hash<T>
/*    */ {
/*    */   int hash32(T paramT);
/*    */   
/*    */   default Number hash(T t) {
/* 22 */     return Integer.valueOf(hash32(t));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\Hash32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */