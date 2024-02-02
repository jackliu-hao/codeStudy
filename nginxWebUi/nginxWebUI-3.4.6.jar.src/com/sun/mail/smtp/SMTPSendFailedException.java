/*    */ package com.sun.mail.smtp;
/*    */ 
/*    */ import javax.mail.Address;
/*    */ import javax.mail.SendFailedException;
/*    */ import javax.mail.internet.InternetAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SMTPSendFailedException
/*    */   extends SendFailedException
/*    */ {
/*    */   protected InternetAddress addr;
/*    */   protected String cmd;
/*    */   protected int rc;
/*    */   private static final long serialVersionUID = 8049122628728932894L;
/*    */   
/*    */   public SMTPSendFailedException(String cmd, int rc, String err, Exception ex, Address[] vs, Address[] vus, Address[] inv) {
/* 77 */     super(err, ex, vs, vus, inv);
/* 78 */     this.cmd = cmd;
/* 79 */     this.rc = rc;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 86 */     return this.cmd;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getReturnCode() {
/* 96 */     return this.rc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPSendFailedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */