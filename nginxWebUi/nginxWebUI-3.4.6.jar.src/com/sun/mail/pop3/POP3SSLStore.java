/*    */ package com.sun.mail.pop3;
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
/*    */ public class POP3SSLStore
/*    */   extends POP3Store
/*    */ {
/*    */   public POP3SSLStore(Session session, URLName url) {
/* 53 */     super(session, url, "pop3s", true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\POP3SSLStore.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */