/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Version
/*    */ {
/*    */   private static final String JAR_NAME;
/*    */   public static final String VERSION;
/*    */   
/*    */   public static void main(String[] args) {
/* 41 */     System.out.print(VERSION);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 49 */     Properties versionProps = new Properties();
/* 50 */     String jarName = "(unknown)";
/* 51 */     String versionString = "(unknown)";
/*    */     try {
/* 53 */       InputStream stream = Version.class.getResourceAsStream("Version.properties");
/*    */       try {
/* 55 */         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
/*    */         try {
/* 57 */           versionProps.load(reader);
/* 58 */           jarName = versionProps.getProperty("jarName", jarName);
/* 59 */           versionString = versionProps.getProperty("version", versionString);
/*    */         } finally {
/*    */           try {
/* 62 */             reader.close();
/* 63 */           } catch (Throwable throwable) {}
/*    */         } 
/*    */       } finally {
/*    */         
/*    */         try {
/* 68 */           stream.close();
/* 69 */         } catch (Throwable throwable) {}
/*    */       }
/*    */     
/* 72 */     } catch (IOException iOException) {}
/*    */     
/* 74 */     JAR_NAME = jarName;
/* 75 */     VERSION = versionString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getJarName() {
/* 84 */     return JAR_NAME;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getVersionString() {
/* 93 */     return VERSION;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */