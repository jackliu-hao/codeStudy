/*    */ package cn.hutool.core.lang;
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
/*    */ public class DefaultSegment<T extends Number>
/*    */   implements Segment<T>
/*    */ {
/*    */   protected T startIndex;
/*    */   protected T endIndex;
/*    */   
/*    */   public DefaultSegment(T startIndex, T endIndex) {
/* 21 */     this.startIndex = startIndex;
/* 22 */     this.endIndex = endIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getStartIndex() {
/* 27 */     return this.startIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getEndIndex() {
/* 32 */     return this.endIndex;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\DefaultSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */