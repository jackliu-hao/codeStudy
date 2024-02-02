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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileExtensionMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final String extension;
/*    */   private boolean caseInsensitive = true;
/*    */   
/*    */   public FileExtensionMatcher(String extension) {
/* 40 */     if (extension.indexOf('/') != -1) {
/* 41 */       throw new IllegalArgumentException("A file extension can't contain \"/\": " + extension);
/*    */     }
/* 43 */     if (extension.indexOf('*') != -1) {
/* 44 */       throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
/*    */     }
/* 46 */     if (extension.indexOf('?') != -1) {
/* 47 */       throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
/*    */     }
/* 49 */     if (extension.startsWith(".")) {
/* 50 */       throw new IllegalArgumentException("A file extension can't start with \".\": " + extension);
/*    */     }
/* 52 */     this.extension = extension;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 57 */     int ln = sourceName.length();
/* 58 */     int extLn = this.extension.length();
/* 59 */     if (ln < extLn + 1 || sourceName.charAt(ln - extLn - 1) != '.') {
/* 60 */       return false;
/*    */     }
/*    */     
/* 63 */     return sourceName.regionMatches(this.caseInsensitive, ln - extLn, this.extension, 0, extLn);
/*    */   }
/*    */   
/*    */   public boolean isCaseInsensitive() {
/* 67 */     return this.caseInsensitive;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCaseInsensitive(boolean caseInsensitive) {
/* 74 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileExtensionMatcher caseInsensitive(boolean caseInsensitive) {
/* 81 */     setCaseInsensitive(caseInsensitive);
/* 82 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\FileExtensionMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */