/*     */ package javax.mail.search;
/*     */ 
/*     */ import javax.mail.Message;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.Part;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BodyTerm
/*     */   extends StringTerm
/*     */ {
/*     */   private static final long serialVersionUID = -4888862527916911385L;
/*     */   
/*     */   public BodyTerm(String pattern) {
/*  64 */     super(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Message msg) {
/*  74 */     return matchPart((Part)msg);
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
/*     */   private boolean matchPart(Part p) {
/*     */     try {
/*  87 */       if (p.isMimeType("text/*")) {
/*  88 */         String s = (String)p.getContent();
/*  89 */         if (s == null) {
/*  90 */           return false;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  99 */         return match(s);
/* 100 */       }  if (p.isMimeType("multipart/*")) {
/* 101 */         Multipart mp = (Multipart)p.getContent();
/* 102 */         int count = mp.getCount();
/* 103 */         for (int i = 0; i < count; i++)
/* 104 */         { if (matchPart((Part)mp.getBodyPart(i)))
/* 105 */             return true;  } 
/* 106 */       } else if (p.isMimeType("message/rfc822")) {
/* 107 */         return matchPart((Part)p.getContent());
/*     */       } 
/* 109 */     } catch (Exception ex) {}
/*     */     
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 118 */     if (!(obj instanceof BodyTerm))
/* 119 */       return false; 
/* 120 */     return super.equals(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\BodyTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */