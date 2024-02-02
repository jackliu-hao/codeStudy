/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
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
/*    */ public class LengthFinder
/*    */   extends TextFinder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final int length;
/*    */   
/*    */   public LengthFinder(int length) {
/* 22 */     this.length = length;
/*    */   }
/*    */ 
/*    */   
/*    */   public int start(int from) {
/* 27 */     Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
/* 28 */     int limit = getValidEndIndex();
/*    */     
/* 30 */     if (this.negative) {
/* 31 */       int result = from - this.length;
/* 32 */       if (result > limit) {
/* 33 */         return result;
/*    */       }
/*    */     } else {
/* 36 */       int result = from + this.length;
/* 37 */       if (result < limit) {
/* 38 */         return result;
/*    */       }
/*    */     } 
/* 41 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int end(int start) {
/* 46 */     return start;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\LengthFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */