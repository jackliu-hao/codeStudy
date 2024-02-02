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
/*    */ public class NotMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final TemplateSourceMatcher matcher;
/*    */   
/*    */   public NotMatcher(TemplateSourceMatcher matcher) {
/* 33 */     this.matcher = matcher;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 38 */     return !this.matcher.matches(sourceName, templateSource);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\NotMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */