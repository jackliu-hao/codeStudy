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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class Java8Base64Decoder
/*    */   extends Base64Decoder
/*    */ {
/*    */   byte[] decode(String s) {
/*    */     try {
/* 29 */       Object decoder = Class.forName("java.util.Base64").getMethod("getDecoder", new Class[0]).invoke(null, new Object[0]);
/* 30 */       return (byte[])Class.forName("java.util.Base64.Decoder")
/* 31 */         .getMethod("decode", new Class[] { String.class }).invoke(decoder, new Object[] { s });
/* 32 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|ClassNotFoundException e) {
/*    */       
/* 34 */       throw new IllegalStateException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\Java8Base64Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */