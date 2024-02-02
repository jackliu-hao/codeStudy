/*    */ package javax.mail.search;
/*    */ 
/*    */ import javax.mail.Message;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SubjectTerm
/*    */   extends StringTerm
/*    */ {
/*    */   private static final long serialVersionUID = 7481568618055573432L;
/*    */   
/*    */   public SubjectTerm(String pattern) {
/* 64 */     super(pattern);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean match(Message msg) {
/*    */     String subj;
/*    */     try {
/* 78 */       subj = msg.getSubject();
/* 79 */     } catch (Exception e) {
/* 80 */       return false;
/*    */     } 
/*    */     
/* 83 */     if (subj == null) {
/* 84 */       return false;
/*    */     }
/* 86 */     return match(subj);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 93 */     if (!(obj instanceof SubjectTerm))
/* 94 */       return false; 
/* 95 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\SubjectTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */