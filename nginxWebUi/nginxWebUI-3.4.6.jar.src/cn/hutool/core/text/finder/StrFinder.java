/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.text.CharSequenceUtil;
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
/*    */ public class StrFinder
/*    */   extends TextFinder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final CharSequence strToFind;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public StrFinder(CharSequence strToFind, boolean caseInsensitive) {
/* 25 */     Assert.notEmpty(strToFind);
/* 26 */     this.strToFind = strToFind;
/* 27 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public int start(int from) {
/* 32 */     Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
/* 33 */     int subLen = this.strToFind.length();
/*    */     
/* 35 */     if (from < 0) {
/* 36 */       from = 0;
/*    */     }
/* 38 */     int endLimit = getValidEndIndex();
/* 39 */     if (this.negative) {
/* 40 */       for (int i = from; i > endLimit; i--) {
/* 41 */         if (CharSequenceUtil.isSubEquals(this.text, i, this.strToFind, 0, subLen, this.caseInsensitive)) {
/* 42 */           return i;
/*    */         }
/*    */       } 
/*    */     } else {
/* 46 */       endLimit = endLimit - subLen + 1;
/* 47 */       for (int i = from; i < endLimit; i++) {
/* 48 */         if (CharSequenceUtil.isSubEquals(this.text, i, this.strToFind, 0, subLen, this.caseInsensitive)) {
/* 49 */           return i;
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 54 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int end(int start) {
/* 59 */     if (start < 0) {
/* 60 */       return -1;
/*    */     }
/* 62 */     return start + this.strToFind.length();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\StrFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */