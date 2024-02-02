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
/*     */ public final class FromStringTerm
/*     */   extends AddressStringTerm
/*     */ {
/*     */   private static final long serialVersionUID = 5801127523826772788L;
/*     */   
/*     */   public FromStringTerm(String pattern) {
/*  67 */     super(pattern);
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
/*     */     Address[] from;
/*     */     try {
/*  82 */       from = msg.getFrom();
/*  83 */     } catch (Exception e) {
/*  84 */       return false;
/*     */     } 
/*     */     
/*  87 */     if (from == null) {
/*  88 */       return false;
/*     */     }
/*  90 */     for (int i = 0; i < from.length; i++) {
/*  91 */       if (match(from[i]))
/*  92 */         return true; 
/*  93 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 100 */     if (!(obj instanceof FromStringTerm))
/* 101 */       return false; 
/* 102 */     return super.equals(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\FromStringTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */