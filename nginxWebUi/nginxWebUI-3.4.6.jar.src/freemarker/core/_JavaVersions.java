/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.log.Logger;
/*    */ import freemarker.template.Version;
/*    */ import freemarker.template.utility.SecurityUtilities;
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
/*    */ public final class _JavaVersions
/*    */ {
/*    */   private static final boolean IS_AT_LEAST_8;
/*    */   public static final _Java8 JAVA_8;
/*    */   
/*    */   static {
/*    */     _Java8 java8;
/* 36 */     boolean result = false;
/* 37 */     String vStr = SecurityUtilities.getSystemProperty("java.version", null);
/* 38 */     if (vStr != null) {
/*    */       try {
/* 40 */         Version v = new Version(vStr);
/* 41 */         result = ((v.getMajor() == 1 && v.getMinor() >= 8) || v.getMajor() > 1);
/* 42 */       } catch (Exception exception) {}
/*    */     } else {
/*    */ 
/*    */       
/*    */       try {
/* 47 */         Class.forName("java.time.Instant");
/* 48 */         result = true;
/* 49 */       } catch (Exception exception) {}
/*    */     } 
/*    */ 
/*    */     
/* 53 */     IS_AT_LEAST_8 = result;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 62 */     if (IS_AT_LEAST_8) {
/*    */       try {
/* 64 */         java8 = (_Java8)Class.forName("freemarker.core._Java8Impl").getField("INSTANCE").get(null);
/* 65 */       } catch (Exception e) {
/*    */         try {
/* 67 */           Logger.getLogger("freemarker.runtime").error("Failed to access Java 8 functionality", e);
/* 68 */         } catch (Exception exception) {}
/*    */ 
/*    */         
/* 71 */         java8 = null;
/*    */       } 
/*    */     } else {
/* 74 */       java8 = null;
/*    */     } 
/* 76 */     JAVA_8 = java8;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_JavaVersions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */