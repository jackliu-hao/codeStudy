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
/*    */ public class FileNameGlobMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final String glob;
/*    */   private Pattern pattern;
/*    */   private boolean caseInsensitive;
/*    */   
/*    */   public FileNameGlobMatcher(String glob) {
/* 47 */     if (glob.indexOf('/') != -1) {
/* 48 */       throw new IllegalArgumentException("A file name glob can't contain \"/\": " + glob);
/*    */     }
/* 50 */     this.glob = glob;
/* 51 */     buildPattern();
/*    */   }
/*    */   
/*    */   private void buildPattern() {
/* 55 */     this.pattern = StringUtil.globToRegularExpression("**/" + this.glob, this.caseInsensitive);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 60 */     return this.pattern.matcher(sourceName).matches();
/*    */   }
/*    */   
/*    */   public boolean isCaseInsensitive() {
/* 64 */     return this.caseInsensitive;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCaseInsensitive(boolean caseInsensitive) {
/* 71 */     boolean lastCaseInsensitive = this.caseInsensitive;
/* 72 */     this.caseInsensitive = caseInsensitive;
/* 73 */     if (lastCaseInsensitive != caseInsensitive) {
/* 74 */       buildPattern();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileNameGlobMatcher caseInsensitive(boolean caseInsensitive) {
/* 82 */     setCaseInsensitive(caseInsensitive);
/* 83 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\FileNameGlobMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */