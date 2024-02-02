/*    */ package org.xnio.nio;
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
/*    */ 
/*    */ public final class Version
/*    */ {
/*    */   private static final String JAR_NAME;
/*    */   private static final String VERSION_STRING;
/*    */   
/*    */   public static void main(String[] args) {
/* 42 */     System.out.print(VERSION_STRING);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 50 */     Properties versionProps = new Properties();
/* 51 */     String jarName = "(unknown)";
/* 52 */     String versionString = "(unknown)";
/*    */     try {
/* 54 */       InputStream stream = Version.class.getResourceAsStream("Version.properties");
/*    */       try {
/* 56 */         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
/*    */         try {
/* 58 */           versionProps.load(reader);
/* 59 */           jarName = versionProps.getProperty("jarName", jarName);
/* 60 */           versionString = versionProps.getProperty("version", versionString);
/*    */         } finally {
/*    */           try {
/* 63 */             reader.close();
/* 64 */           } catch (Throwable throwable) {}
/*    */         } 
/*    */       } finally {
/*    */         
/*    */         try {
/* 69 */           stream.close();
/* 70 */         } catch (Throwable throwable) {}
/*    */       }
/*    */     
/* 73 */     } catch (IOException iOException) {}
/*    */     
/* 75 */     JAR_NAME = jarName;
/* 76 */     VERSION_STRING = versionString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getJarName() {
/* 85 */     return JAR_NAME;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getVersionString() {
/* 94 */     return VERSION_STRING;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */