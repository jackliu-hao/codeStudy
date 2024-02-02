/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import javax.mail.SendFailedException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SMTPSenderFailedException
/*     */   extends SendFailedException
/*     */ {
/*     */   protected InternetAddress addr;
/*     */   protected String cmd;
/*     */   protected int rc;
/*     */   private static final long serialVersionUID = 514540454964476947L;
/*     */   
/*     */   public SMTPSenderFailedException(InternetAddress addr, String cmd, int rc, String err) {
/*  73 */     super(err);
/*  74 */     this.addr = addr;
/*  75 */     this.cmd = cmd;
/*  76 */     this.rc = rc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetAddress getAddress() {
/*  83 */     return this.addr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  90 */     return this.cmd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getReturnCode() {
/* 101 */     return this.rc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPSenderFailedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */