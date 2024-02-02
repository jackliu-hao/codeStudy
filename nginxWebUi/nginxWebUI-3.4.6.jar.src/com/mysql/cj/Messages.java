/*    */ package com.mysql.cj;
/*    */ 
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Locale;
/*    */ import java.util.MissingResourceException;
/*    */ import java.util.ResourceBundle;
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
/*    */ 
/*    */ public class Messages
/*    */ {
/*    */   private static final String BUNDLE_NAME = "com.mysql.cj.LocalizedErrorMessages";
/*    */   private static final ResourceBundle RESOURCE_BUNDLE;
/* 44 */   private static final Object[] emptyObjectArray = new Object[0];
/*    */   
/*    */   static {
/* 47 */     ResourceBundle temp = null;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 55 */       temp = ResourceBundle.getBundle("com.mysql.cj.LocalizedErrorMessages", Locale.getDefault(), Messages.class.getClassLoader());
/* 56 */     } catch (Throwable t) {
/*    */       try {
/* 58 */         temp = ResourceBundle.getBundle("com.mysql.cj.LocalizedErrorMessages");
/* 59 */       } catch (Throwable t2) {
/* 60 */         RuntimeException rt = new RuntimeException("Can't load resource bundle due to underlying exception " + t.toString());
/* 61 */         rt.initCause(t2);
/*    */         
/* 63 */         throw rt;
/*    */       } 
/*    */     } finally {
/* 66 */       RESOURCE_BUNDLE = temp;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getString(String key) {
/* 78 */     return getString(key, emptyObjectArray);
/*    */   }
/*    */   
/*    */   public static String getString(String key, Object[] args) {
/* 82 */     if (RESOURCE_BUNDLE == null) {
/* 83 */       throw new RuntimeException("Localized messages from resource bundle 'com.mysql.cj.LocalizedErrorMessages' not loaded during initialization of driver.");
/*    */     }
/*    */     
/*    */     try {
/* 87 */       if (key == null) {
/* 88 */         throw new IllegalArgumentException("Message key can not be null");
/*    */       }
/*    */       
/* 91 */       String message = RESOURCE_BUNDLE.getString(key);
/*    */       
/* 93 */       if (message == null) {
/* 94 */         message = "Missing error message for key '" + key + "'";
/*    */       }
/*    */       
/* 97 */       return MessageFormat.format(message, args);
/* 98 */     } catch (MissingResourceException e) {
/* 99 */       return '!' + key + '!';
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */