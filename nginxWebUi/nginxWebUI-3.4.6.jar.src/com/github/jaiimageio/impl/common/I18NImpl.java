/*    */ package com.github.jaiimageio.impl.common;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.util.PropertyResourceBundle;
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
/*    */ public class I18NImpl
/*    */ {
/*    */   protected static final String getString(String className, String key) {
/* 69 */     PropertyResourceBundle bundle = null;
/*    */     
/*    */     try {
/* 72 */       InputStream stream = Class.forName(className).getResourceAsStream("properties");
/* 73 */       bundle = new PropertyResourceBundle(stream);
/* 74 */     } catch (Throwable e) {
/* 75 */       throw new RuntimeException(e);
/*    */     } 
/*    */     
/* 78 */     return (String)bundle.handleGetObject(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\I18NImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */