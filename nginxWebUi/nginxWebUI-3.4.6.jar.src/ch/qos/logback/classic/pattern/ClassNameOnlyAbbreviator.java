/*    */ package ch.qos.logback.classic.pattern;
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
/*    */ public class ClassNameOnlyAbbreviator
/*    */   implements Abbreviator
/*    */ {
/*    */   public String abbreviate(String fqClassName) {
/* 30 */     int lastIndex = fqClassName.lastIndexOf('.');
/* 31 */     if (lastIndex != -1) {
/* 32 */       return fqClassName.substring(lastIndex + 1, fqClassName.length());
/*    */     }
/* 34 */     return fqClassName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\ClassNameOnlyAbbreviator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */