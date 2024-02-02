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
/*     */ public class MessageContext
/*     */ {
/*     */   private Part part;
/*     */   
/*     */   public MessageContext(Part part) {
/*  64 */     this.part = part;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Part getPart() {
/*  73 */     return this.part;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message getMessage() {
/*     */     try {
/*  85 */       return getMessage(this.part);
/*  86 */     } catch (MessagingException ex) {
/*  87 */       return null;
/*     */     } 
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
/*     */   private static Message getMessage(Part p) throws MessagingException {
/* 101 */     while (p != null) {
/* 102 */       if (p instanceof Message)
/* 103 */         return (Message)p; 
/* 104 */       BodyPart bp = (BodyPart)p;
/* 105 */       Multipart mp = bp.getParent();
/* 106 */       if (mp == null)
/* 107 */         return null; 
/* 108 */       p = mp.getParent();
/*     */     } 
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session getSession() {
/* 119 */     Message msg = getMessage();
/* 120 */     return (msg != null) ? msg.session : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\MessageContext.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */