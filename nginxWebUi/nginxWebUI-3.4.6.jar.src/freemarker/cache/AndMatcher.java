/*    */ package freemarker.cache;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class AndMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final TemplateSourceMatcher[] matchers;
/*    */   
/*    */   public AndMatcher(TemplateSourceMatcher... matchers) {
/* 33 */     if (matchers.length == 0) throw new IllegalArgumentException("Need at least 1 matcher, had 0."); 
/* 34 */     this.matchers = matchers;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 39 */     for (TemplateSourceMatcher matcher : this.matchers) {
/* 40 */       if (!matcher.matches(sourceName, templateSource)) return false; 
/*    */     } 
/* 42 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\AndMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */