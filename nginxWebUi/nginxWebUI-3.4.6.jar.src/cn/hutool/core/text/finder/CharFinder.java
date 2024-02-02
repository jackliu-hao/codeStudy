/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.NumberUtil;
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
/*    */ public class CharFinder
/*    */   extends TextFinder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final char c;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public CharFinder(char c) {
/* 25 */     this(c, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CharFinder(char c, boolean caseInsensitive) {
/* 35 */     this.c = c;
/* 36 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public int start(int from) {
/* 41 */     Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
/* 42 */     int limit = getValidEndIndex();
/* 43 */     if (this.negative) {
/* 44 */       for (int i = from; i > limit; i--) {
/* 45 */         if (NumberUtil.equals(this.c, this.text.charAt(i), this.caseInsensitive)) {
/* 46 */           return i;
/*    */         }
/*    */       } 
/*    */     } else {
/* 50 */       for (int i = from; i < limit; i++) {
/* 51 */         if (NumberUtil.equals(this.c, this.text.charAt(i), this.caseInsensitive)) {
/* 52 */           return i;
/*    */         }
/*    */       } 
/*    */     } 
/* 56 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int end(int start) {
/* 61 */     if (start < 0) {
/* 62 */       return -1;
/*    */     }
/* 64 */     return start + 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\CharFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */