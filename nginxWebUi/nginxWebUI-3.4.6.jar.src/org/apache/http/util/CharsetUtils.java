/*    */ package org.apache.http.util;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.UnsupportedCharsetException;
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
/*    */ public class CharsetUtils
/*    */ {
/*    */   public static Charset lookup(String name) {
/* 37 */     if (name == null) {
/* 38 */       return null;
/*    */     }
/*    */     try {
/* 41 */       return Charset.forName(name);
/* 42 */     } catch (UnsupportedCharsetException ex) {
/* 43 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Charset get(String name) throws UnsupportedEncodingException {
/* 48 */     if (name == null) {
/* 49 */       return null;
/*    */     }
/*    */     try {
/* 52 */       return Charset.forName(name);
/* 53 */     } catch (UnsupportedCharsetException ex) {
/* 54 */       throw new UnsupportedEncodingException(name);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\CharsetUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */