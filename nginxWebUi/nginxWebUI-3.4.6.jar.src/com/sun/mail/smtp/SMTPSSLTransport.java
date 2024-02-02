/*    */ package com.sun.mail.smtp;
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
/*    */ public class SMTPSSLTransport
/*    */   extends SMTPTransport
/*    */ {
/*    */   public SMTPSSLTransport(Session session, URLName urlname) {
/* 56 */     super(session, urlname, "smtps", true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPSSLTransport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */