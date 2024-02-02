/*     */ package javax.mail;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SendFailedException
/*     */   extends MessagingException
/*     */ {
/*     */   protected transient Address[] invalid;
/*     */   protected transient Address[] validSent;
/*     */   protected transient Address[] validUnsent;
/*     */   private static final long serialVersionUID = -6457531621682372913L;
/*     */   
/*     */   public SendFailedException() {}
/*     */   
/*     */   public SendFailedException(String s) {
/*  77 */     super(s);
/*     */   }
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
/*     */   public SendFailedException(String s, Exception e) {
/*  90 */     super(s, e);
/*     */   }
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
/*     */   public SendFailedException(String msg, Exception ex, Address[] validSent, Address[] validUnsent, Address[] invalid) {
/* 108 */     super(msg, ex);
/* 109 */     this.validSent = validSent;
/* 110 */     this.validUnsent = validUnsent;
/* 111 */     this.invalid = invalid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getValidSentAddresses() {
/* 119 */     return this.validSent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getValidUnsentAddresses() {
/* 129 */     return this.validUnsent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getInvalidAddresses() {
/* 138 */     return this.invalid;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\SendFailedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */