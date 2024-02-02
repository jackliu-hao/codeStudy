/*    */ package cn.hutool.core.text.finder;
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
/*    */ public interface Finder
/*    */ {
/*    */   public static final int INDEX_NOT_FOUND = -1;
/*    */   
/*    */   int start(int paramInt);
/*    */   
/*    */   int end(int paramInt);
/*    */   
/*    */   default Finder reset() {
/* 34 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\Finder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */