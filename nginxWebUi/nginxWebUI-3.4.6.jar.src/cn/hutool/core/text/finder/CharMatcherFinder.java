/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.lang.Matcher;
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
/*    */ public class CharMatcherFinder
/*    */   extends TextFinder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Matcher<Character> matcher;
/*    */   
/*    */   public CharMatcherFinder(Matcher<Character> matcher) {
/* 23 */     this.matcher = matcher;
/*    */   }
/*    */ 
/*    */   
/*    */   public int start(int from) {
/* 28 */     Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
/* 29 */     int limit = getValidEndIndex();
/* 30 */     if (this.negative) {
/* 31 */       for (int i = from; i > limit; i--) {
/* 32 */         if (this.matcher.match(Character.valueOf(this.text.charAt(i)))) {
/* 33 */           return i;
/*    */         }
/*    */       } 
/*    */     } else {
/* 37 */       for (int i = from; i < limit; i++) {
/* 38 */         if (this.matcher.match(Character.valueOf(this.text.charAt(i)))) {
/* 39 */           return i;
/*    */         }
/*    */       } 
/*    */     } 
/* 43 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int end(int start) {
/* 48 */     if (start < 0) {
/* 49 */       return -1;
/*    */     }
/* 51 */     return start + 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\CharMatcherFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */