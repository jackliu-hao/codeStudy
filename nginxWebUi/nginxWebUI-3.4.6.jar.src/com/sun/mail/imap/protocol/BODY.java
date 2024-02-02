/*    */ package com.sun.mail.imap.protocol;
/*    */ 
/*    */ import com.sun.mail.iap.ByteArray;
/*    */ import com.sun.mail.iap.ParsingException;
/*    */ import java.io.ByteArrayInputStream;
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
/*    */ public class BODY
/*    */   implements Item
/*    */ {
/* 55 */   static final char[] name = new char[] { 'B', 'O', 'D', 'Y' };
/*    */   
/*    */   public int msgno;
/*    */   public ByteArray data;
/*    */   public String section;
/* 60 */   public int origin = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BODY(FetchResponse r) throws ParsingException {
/* 66 */     this.msgno = r.getNumber();
/*    */     
/* 68 */     r.skipSpaces();
/*    */     
/*    */     int b;
/* 71 */     while ((b = r.readByte()) != 93) {
/* 72 */       if (b == 0) {
/* 73 */         throw new ParsingException("BODY parse error: missing ``]'' at section end");
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 78 */     if (r.readByte() == 60) {
/* 79 */       this.origin = r.readNumber();
/* 80 */       r.skip(1);
/*    */     } 
/*    */     
/* 83 */     this.data = r.readByteArray();
/*    */   }
/*    */   
/*    */   public ByteArray getByteArray() {
/* 87 */     return this.data;
/*    */   }
/*    */   
/*    */   public ByteArrayInputStream getByteArrayInputStream() {
/* 91 */     if (this.data != null) {
/* 92 */       return this.data.toByteArrayInputStream();
/*    */     }
/* 94 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\BODY.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */