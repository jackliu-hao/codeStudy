/*    */ package com.sun.mail.imap;
/*    */ 
/*    */ import javax.mail.Session;
/*    */ import javax.mail.URLName;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IMAPSSLStore
/*    */   extends IMAPStore
/*    */ {
/*    */   public IMAPSSLStore(Session session, URLName url) {
/* 56 */     super(session, url, "imaps", true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPSSLStore.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */