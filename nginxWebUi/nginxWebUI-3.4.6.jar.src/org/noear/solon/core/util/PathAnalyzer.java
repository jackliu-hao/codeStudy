/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
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
/*    */ public class PathAnalyzer
/*    */ {
/* 19 */   private static Map<String, PathAnalyzer> cached = new LinkedHashMap<>();
/*    */   public static PathAnalyzer get(String expr) {
/* 21 */     PathAnalyzer pa = cached.get(expr);
/* 22 */     if (pa == null) {
/* 23 */       synchronized (expr.intern()) {
/* 24 */         pa = cached.get(expr);
/* 25 */         if (pa == null) {
/* 26 */           pa = new PathAnalyzer(expr);
/* 27 */           cached.put(expr, pa);
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 32 */     return pa;
/*    */   }
/*    */ 
/*    */   
/*    */   private Pattern pattern;
/*    */   
/*    */   private PathAnalyzer(String expr) {
/* 39 */     this.pattern = Pattern.compile(exprCompile(expr), 2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Matcher matcher(String uri) {
/* 46 */     return this.pattern.matcher(uri);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean matches(String uri) {
/* 53 */     return this.pattern.matcher(uri).find();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String exprCompile(String expr) {
/* 61 */     String p = expr;
/*    */     
/* 63 */     p = p.replace(".", "\\.");
/* 64 */     p = p.replace("$", "\\$");
/*    */ 
/*    */     
/* 67 */     p = p.replace("**", ".[]");
/*    */ 
/*    */     
/* 70 */     p = p.replace("*", "[^/]*");
/*    */ 
/*    */     
/* 73 */     if (p.indexOf("{") >= 0) {
/* 74 */       if (p.indexOf("_}") > 0) {
/* 75 */         p = p.replaceAll("\\{[^\\}]+?\\_\\}", "(.+?)");
/*    */       }
/* 77 */       p = p.replaceAll("\\{[^\\}]+?\\}", "([^/]+?)");
/*    */     } 
/*    */     
/* 80 */     if (!p.startsWith("/")) {
/* 81 */       p = "/" + p;
/*    */     }
/*    */     
/* 84 */     p = p.replace(".[]", ".*");
/*    */ 
/*    */     
/* 87 */     return "^" + p + "$";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\PathAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */