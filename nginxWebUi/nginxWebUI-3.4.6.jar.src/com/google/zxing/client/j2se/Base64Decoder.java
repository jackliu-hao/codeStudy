/*    */ package com.google.zxing.client.j2se;
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
/*    */ abstract class Base64Decoder
/*    */ {
/*    */   private static final Base64Decoder INSTANCE;
/*    */   
/*    */   static {
/*    */     Base64Decoder instance;
/*    */     try {
/* 28 */       Class.forName("java.util.Base64");
/*    */       
/* 30 */       instance = new Java8Base64Decoder();
/* 31 */     } catch (ClassNotFoundException cnfe) {
/* 32 */       instance = new JAXBBase64Decoder();
/*    */     } 
/* 34 */     INSTANCE = instance;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Base64Decoder getInstance() {
/* 44 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   abstract byte[] decode(String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\Base64Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */