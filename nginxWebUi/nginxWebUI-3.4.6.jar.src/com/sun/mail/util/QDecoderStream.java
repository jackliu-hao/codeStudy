/*    */ package com.sun.mail.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public class QDecoderStream
/*    */   extends QPDecoderStream
/*    */ {
/*    */   public QDecoderStream(InputStream in) {
/* 59 */     super(in);
/*    */   }
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
/*    */   public int read() throws IOException {
/* 75 */     int c = this.in.read();
/*    */     
/* 77 */     if (c == 95)
/* 78 */       return 32; 
/* 79 */     if (c == 61) {
/*    */       
/* 81 */       this.ba[0] = (byte)this.in.read();
/* 82 */       this.ba[1] = (byte)this.in.read();
/*    */       
/*    */       try {
/* 85 */         return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
/* 86 */       } catch (NumberFormatException nex) {
/* 87 */         throw new DecodingException("QDecoder: Error in QP stream " + nex.getMessage());
/*    */       } 
/*    */     } 
/*    */     
/* 91 */     return c;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\QDecoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */