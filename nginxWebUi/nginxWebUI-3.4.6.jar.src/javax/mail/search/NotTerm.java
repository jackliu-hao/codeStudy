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
/*    */ public final class NotTerm
/*    */   extends SearchTerm
/*    */ {
/*    */   protected SearchTerm term;
/*    */   private static final long serialVersionUID = 7152293214217310216L;
/*    */   
/*    */   public NotTerm(SearchTerm t) {
/* 62 */     this.term = t;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SearchTerm getTerm() {
/* 69 */     return this.term;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean match(Message msg) {
/* 74 */     return !this.term.match(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 81 */     if (!(obj instanceof NotTerm))
/* 82 */       return false; 
/* 83 */     NotTerm nt = (NotTerm)obj;
/* 84 */     return nt.term.equals(this.term);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 91 */     return this.term.hashCode() << 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\NotTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */