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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RecipientStringTerm
/*     */   extends AddressStringTerm
/*     */ {
/*     */   private Message.RecipientType type;
/*     */   private static final long serialVersionUID = -8293562089611618849L;
/*     */   
/*     */   public RecipientStringTerm(Message.RecipientType type, String pattern) {
/*  75 */     super(pattern);
/*  76 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message.RecipientType getRecipientType() {
/*  83 */     return this.type;
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
/*     */   public boolean match(Message msg) {
/*     */     Address[] recipients;
/*     */     try {
/*  98 */       recipients = msg.getRecipients(this.type);
/*  99 */     } catch (Exception e) {
/* 100 */       return false;
/*     */     } 
/*     */     
/* 103 */     if (recipients == null) {
/* 104 */       return false;
/*     */     }
/* 106 */     for (int i = 0; i < recipients.length; i++) {
/* 107 */       if (match(recipients[i]))
/* 108 */         return true; 
/* 109 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 116 */     if (!(obj instanceof RecipientStringTerm))
/* 117 */       return false; 
/* 118 */     RecipientStringTerm rst = (RecipientStringTerm)obj;
/* 119 */     return (rst.type.equals(this.type) && super.equals(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 126 */     return this.type.hashCode() + super.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\RecipientStringTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */