/*     */ package javax.mail.search;
/*     */ 
/*     */ import javax.mail.Address;
/*     */ import javax.mail.Message;
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
/*     */ public final class RecipientTerm
/*     */   extends AddressTerm
/*     */ {
/*     */   protected Message.RecipientType type;
/*     */   private static final long serialVersionUID = 6548700653122680468L;
/*     */   
/*     */   public RecipientTerm(Message.RecipientType type, Address address) {
/*  70 */     super(address);
/*  71 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message.RecipientType getRecipientType() {
/*  78 */     return this.type;
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
/*     */   public boolean match(Message msg) {
/*     */     Address[] recipients;
/*     */     try {
/*  92 */       recipients = msg.getRecipients(this.type);
/*  93 */     } catch (Exception e) {
/*  94 */       return false;
/*     */     } 
/*     */     
/*  97 */     if (recipients == null) {
/*  98 */       return false;
/*     */     }
/* 100 */     for (int i = 0; i < recipients.length; i++) {
/* 101 */       if (match(recipients[i]))
/* 102 */         return true; 
/* 103 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 110 */     if (!(obj instanceof RecipientTerm))
/* 111 */       return false; 
/* 112 */     RecipientTerm rt = (RecipientTerm)obj;
/* 113 */     return (rt.type.equals(this.type) && super.equals(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 120 */     return this.type.hashCode() + super.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\RecipientTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */