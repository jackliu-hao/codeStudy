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
/*    */ final class JAXBBase64Decoder
/*    */   extends Base64Decoder
/*    */ {
/*    */   byte[] decode(String s) {
/*    */     try {
/* 29 */       return (byte[])Class.forName("javax.xml.bind.DatatypeConverter")
/* 30 */         .getMethod("parseBase64Binary", new Class[] { String.class }).invoke(null, new Object[] { s });
/* 31 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|ClassNotFoundException e) {
/*    */       
/* 33 */       throw new IllegalStateException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\JAXBBase64Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */