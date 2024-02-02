/*    */ package freemarker.cache;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
/*    */ import java.io.IOException;
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
/*    */ public class PathGlobMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final String glob;
/*    */   private Pattern pattern;
/*    */   private boolean caseInsensitive;
/*    */   
/*    */   public PathGlobMatcher(String glob) {
/* 61 */     if (glob.startsWith("/")) {
/* 62 */       throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + glob);
/*    */     }
/* 64 */     this.glob = glob;
/* 65 */     buildPattern();
/*    */   }
/*    */   
/*    */   private void buildPattern() {
/* 69 */     this.pattern = StringUtil.globToRegularExpression(this.glob, this.caseInsensitive);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 74 */     return this.pattern.matcher(sourceName).matches();
/*    */   }
/*    */   
/*    */   public boolean isCaseInsensitive() {
/* 78 */     return this.caseInsensitive;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCaseInsensitive(boolean caseInsensitive) {
/* 85 */     boolean lastCaseInsensitive = this.caseInsensitive;
/* 86 */     this.caseInsensitive = caseInsensitive;
/* 87 */     if (lastCaseInsensitive != caseInsensitive) {
/* 88 */       buildPattern();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PathGlobMatcher caseInsensitive(boolean caseInsensitive) {
/* 96 */     setCaseInsensitive(caseInsensitive);
/* 97 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\PathGlobMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */