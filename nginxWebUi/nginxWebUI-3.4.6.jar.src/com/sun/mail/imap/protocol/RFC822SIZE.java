/*    */ package com.sun.mail.imap.protocol;
/*    */ 
/*    */ import com.sun.mail.iap.ParsingException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RFC822SIZE
/*    */   implements Item
/*    */ {
/* 53 */   static final char[] name = new char[] { 'R', 'F', 'C', '8', '2', '2', '.', 'S', 'I', 'Z', 'E' };
/*    */ 
/*    */   
/*    */   public int msgno;
/*    */   
/*    */   public int size;
/*    */ 
/*    */   
/*    */   public RFC822SIZE(FetchResponse r) throws ParsingException {
/* 62 */     this.msgno = r.getNumber();
/* 63 */     r.skipSpaces();
/* 64 */     this.size = r.readNumber();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\RFC822SIZE.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */