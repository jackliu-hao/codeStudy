/*    */ package org.apache.commons.codec.net;
/*    */ 
/*    */ import org.apache.commons.codec.DecoderException;
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
/*    */ class Utils
/*    */ {
/*    */   private static final int RADIX = 16;
/*    */   
/*    */   static int digit16(byte b) throws DecoderException {
/* 47 */     int i = Character.digit((char)b, 16);
/* 48 */     if (i == -1) {
/* 49 */       throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + b);
/*    */     }
/* 51 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static char hexDigit(int b) {
/* 61 */     return Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\net\Utils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */