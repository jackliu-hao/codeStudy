/*    */ package io.undertow.servlet.handlers;
/*    */ 
/*    */ import java.util.StringTokenizer;
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
/*    */ final class Paths
/*    */ {
/*    */   private static final String META_INF = "META-INF";
/*    */   private static final String WEB_INF = "WEB-INF";
/*    */   
/*    */   static boolean isForbidden(String path) {
/* 36 */     StringTokenizer st = new StringTokenizer(path, "/\\", false);
/*    */     
/* 38 */     while (st.hasMoreTokens()) {
/* 39 */       String subPath = st.nextToken();
/* 40 */       if ("META-INF".equalsIgnoreCase(subPath) || "WEB-INF".equalsIgnoreCase(subPath)) {
/* 41 */         return true;
/*    */       }
/*    */     } 
/* 44 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\Paths.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */