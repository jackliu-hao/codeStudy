/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TextFinder
/*    */   implements Finder, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected CharSequence text;
/* 17 */   protected int endIndex = -1;
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean negative;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TextFinder setText(CharSequence text) {
/* 27 */     this.text = (CharSequence)Assert.notNull(text, "Text must be not null!", new Object[0]);
/* 28 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TextFinder setEndIndex(int endIndex) {
/* 40 */     this.endIndex = endIndex;
/* 41 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TextFinder setNegative(boolean negative) {
/* 51 */     this.negative = negative;
/* 52 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int getValidEndIndex() {
/*    */     int limit;
/* 62 */     if (this.negative && -1 == this.endIndex)
/*    */     {
/* 64 */       return -1;
/*    */     }
/*    */     
/* 67 */     if (this.endIndex < 0) {
/* 68 */       limit = this.endIndex + this.text.length() + 1;
/*    */     } else {
/* 70 */       limit = Math.min(this.endIndex, this.text.length());
/*    */     } 
/* 72 */     return limit;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\TextFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */