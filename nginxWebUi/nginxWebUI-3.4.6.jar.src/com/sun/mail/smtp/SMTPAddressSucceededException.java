/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import javax.mail.MessagingException;
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
/*     */ 
/*     */ public class SMTPAddressSucceededException
/*     */   extends MessagingException
/*     */ {
/*     */   protected InternetAddress addr;
/*     */   protected String cmd;
/*     */   protected int rc;
/*     */   private static final long serialVersionUID = -1168335848623096749L;
/*     */   
/*     */   public SMTPAddressSucceededException(InternetAddress addr, String cmd, int rc, String err) {
/*  74 */     super(err);
/*  75 */     this.addr = addr;
/*  76 */     this.cmd = cmd;
/*  77 */     this.rc = rc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetAddress getAddress() {
/*  84 */     return this.addr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  91 */     return this.cmd;
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
/* 102 */     return this.rc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPAddressSucceededException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */