/*    */ package org.apache.http.client.utils;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class Punycode
/*    */ {
/*    */   private static final Idn impl;
/*    */   
/*    */   static {
/*    */     Idn idn;
/*    */     try {
/* 47 */       idn = new JdkIdn();
/* 48 */     } catch (Exception e) {
/* 49 */       idn = new Rfc3492Idn();
/*    */     } 
/* 51 */     impl = idn;
/*    */   }
/*    */   
/*    */   public static String toUnicode(String punycode) {
/* 55 */     return impl.toUnicode(punycode);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\Punycode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */