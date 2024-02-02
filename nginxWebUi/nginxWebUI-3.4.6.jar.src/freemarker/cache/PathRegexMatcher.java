/*    */ package freemarker.cache;
/*    */ 
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
/*    */ public class PathRegexMatcher
/*    */   extends TemplateSourceMatcher
/*    */ {
/*    */   private final Pattern pattern;
/*    */   
/*    */   public PathRegexMatcher(String regex) {
/* 43 */     if (regex.startsWith("/")) {
/* 44 */       throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + regex);
/*    */     }
/* 46 */     this.pattern = Pattern.compile(regex);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(String sourceName, Object templateSource) throws IOException {
/* 51 */     return this.pattern.matcher(sourceName).matches();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\PathRegexMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */