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
/*    */ public class UID
/*    */   implements Item
/*    */ {
/* 53 */   static final char[] name = new char[] { 'U', 'I', 'D' };
/*    */ 
/*    */   
/*    */   public int seqnum;
/*    */   
/*    */   public long uid;
/*    */ 
/*    */   
/*    */   public UID(FetchResponse r) throws ParsingException {
/* 62 */     this.seqnum = r.getNumber();
/* 63 */     r.skipSpaces();
/* 64 */     this.uid = r.readLong();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\UID.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */