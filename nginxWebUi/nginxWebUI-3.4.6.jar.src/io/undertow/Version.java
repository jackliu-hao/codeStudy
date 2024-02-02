/*    */ package io.undertow;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.util.Properties;
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
/*    */ public class Version
/*    */ {
/*    */   private static final String versionString;
/*    */   private static final String SERVER_NAME = "Undertow";
/*    */   
/*    */   static {
/* 33 */     String version = "Unknown";
/* 34 */     try (InputStream versionPropsStream = Version.class.getResourceAsStream("version.properties")) {
/* 35 */       Properties props = new Properties();
/* 36 */       props.load(versionPropsStream);
/* 37 */       version = props.getProperty("undertow.version");
/* 38 */     } catch (Exception e) {
/* 39 */       e.printStackTrace();
/*    */     } 
/* 41 */     versionString = version;
/* 42 */   } private static final String fullVersionString = "Undertow - " + versionString;
/*    */ 
/*    */   
/*    */   public static String getVersionString() {
/* 46 */     return versionString;
/*    */   }
/*    */   
/*    */   public static String getFullVersionString() {
/* 50 */     return fullVersionString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */