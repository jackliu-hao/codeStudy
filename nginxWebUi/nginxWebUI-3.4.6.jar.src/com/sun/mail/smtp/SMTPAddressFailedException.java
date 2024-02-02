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
/*     */ 
/*     */ 
/*     */ public class SMTPAddressFailedException
/*     */   extends SendFailedException
/*     */ {
/*     */   protected InternetAddress addr;
/*     */   protected String cmd;
/*     */   protected int rc;
/*     */   private static final long serialVersionUID = 804831199768630097L;
/*     */   
/*     */   public SMTPAddressFailedException(InternetAddress addr, String cmd, int rc, String err) {
/*  75 */     super(err);
/*  76 */     this.addr = addr;
/*  77 */     this.cmd = cmd;
/*  78 */     this.rc = rc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternetAddress getAddress() {
/*  85 */     return this.addr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  92 */     return this.cmd;
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
/* 103 */     return this.rc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPAddressFailedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */