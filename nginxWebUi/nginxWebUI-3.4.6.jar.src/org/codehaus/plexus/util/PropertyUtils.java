/*    */ package org.codehaus.plexus.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
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
/*    */ public class PropertyUtils
/*    */ {
/*    */   public static Properties loadProperties(URL url) {
/*    */     try {
/* 41 */       return loadProperties(url.openStream());
/*    */     }
/* 43 */     catch (Exception e) {
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 48 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static Properties loadProperties(File file) {
/*    */     try {
/* 55 */       return loadProperties(new FileInputStream(file));
/*    */     }
/* 57 */     catch (Exception e) {
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 62 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static Properties loadProperties(InputStream is) {
/*    */     try {
/* 69 */       Properties properties = new Properties();
/*    */ 
/*    */       
/* 72 */       if (is != null)
/*    */       {
/* 74 */         properties.load(is);
/*    */       }
/*    */       
/* 77 */       return properties;
/*    */     }
/* 79 */     catch (IOException e) {
/*    */ 
/*    */     
/*    */     } finally {
/*    */ 
/*    */       
/*    */       try {
/*    */         
/* 87 */         if (is != null)
/*    */         {
/* 89 */           is.close();
/*    */         }
/*    */       }
/* 92 */       catch (IOException e) {}
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 98 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\PropertyUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */