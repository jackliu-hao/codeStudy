/*    */ package cn.hutool.core.text.finder;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
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
/*    */ public class PatternFinder
/*    */   extends TextFinder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Pattern pattern;
/*    */   private Matcher matcher;
/*    */   
/*    */   public PatternFinder(String regex, boolean caseInsensitive) {
/* 26 */     this(Pattern.compile(regex, caseInsensitive ? 2 : 0));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PatternFinder(Pattern pattern) {
/* 35 */     this.pattern = pattern;
/*    */   }
/*    */ 
/*    */   
/*    */   public TextFinder setText(CharSequence text) {
/* 40 */     this.matcher = this.pattern.matcher(text);
/* 41 */     return super.setText(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public TextFinder setNegative(boolean negative) {
/* 46 */     throw new UnsupportedOperationException("Negative is invalid for Pattern!");
/*    */   }
/*    */ 
/*    */   
/*    */   public int start(int from) {
/* 51 */     if (this.matcher.find(from))
/*    */     {
/* 53 */       if (this.matcher.end() <= getValidEndIndex()) {
/* 54 */         return this.matcher.start();
/*    */       }
/*    */     }
/* 57 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int end(int start) {
/* 62 */     int limit, end = this.matcher.end();
/*    */     
/* 64 */     if (this.endIndex < 0) {
/* 65 */       limit = this.text.length();
/*    */     } else {
/* 67 */       limit = Math.min(this.endIndex, this.text.length());
/*    */     } 
/* 69 */     return (end <= limit) ? end : -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public PatternFinder reset() {
/* 74 */     this.matcher.reset();
/* 75 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\finder\PatternFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */