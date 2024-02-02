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
/*    */ public class RFC822DATA
/*    */   implements Item
/*    */ {
/* 55 */   static final char[] name = new char[] { 'R', 'F', 'C', '8', '2', '2' };
/*    */   
/*    */   public int msgno;
/*    */   
/*    */   public ByteArray data;
/*    */ 
/*    */   
/*    */   public RFC822DATA(FetchResponse r) throws ParsingException {
/* 63 */     this.msgno = r.getNumber();
/* 64 */     r.skipSpaces();
/* 65 */     this.data = r.readByteArray();
/*    */   }
/*    */   
/*    */   public ByteArray getByteArray() {
/* 69 */     return this.data;
/*    */   }
/*    */   
/*    */   public ByteArrayInputStream getByteArrayInputStream() {
/* 73 */     if (this.data != null) {
/* 74 */       return this.data.toByteArrayInputStream();
/*    */     }
/* 76 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\RFC822DATA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */