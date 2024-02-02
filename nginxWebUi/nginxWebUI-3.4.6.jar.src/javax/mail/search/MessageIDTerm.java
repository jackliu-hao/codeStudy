/*     */ package javax.mail.search;
/*     */ 
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
/*     */ public final class MessageIDTerm
/*     */   extends StringTerm
/*     */ {
/*     */   private static final long serialVersionUID = -2121096296454691963L;
/*     */   
/*     */   public MessageIDTerm(String msgid) {
/*  67 */     super(msgid);
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
/*     */     String[] s;
/*     */     try {
/*  81 */       s = msg.getHeader("Message-ID");
/*  82 */     } catch (Exception e) {
/*  83 */       return false;
/*     */     } 
/*     */     
/*  86 */     if (s == null) {
/*  87 */       return false;
/*     */     }
/*  89 */     for (int i = 0; i < s.length; i++) {
/*  90 */       if (match(s[i]))
/*  91 */         return true; 
/*  92 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  99 */     if (!(obj instanceof MessageIDTerm))
/* 100 */       return false; 
/* 101 */     return super.equals(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\MessageIDTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */