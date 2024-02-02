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
/*    */ public interface Hash64<T>
/*    */   extends Hash<T>
/*    */ {
/*    */   long hash64(T paramT);
/*    */   
/*    */   default Number hash(T t) {
/* 22 */     return Long.valueOf(hash64(t));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\Hash64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */