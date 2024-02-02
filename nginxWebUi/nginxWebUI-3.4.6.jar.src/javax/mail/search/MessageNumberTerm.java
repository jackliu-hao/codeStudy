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
/*    */ public final class MessageNumberTerm
/*    */   extends IntegerComparisonTerm
/*    */ {
/*    */   private static final long serialVersionUID = -5379625829658623812L;
/*    */   
/*    */   public MessageNumberTerm(int number) {
/* 61 */     super(3, number);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean match(Message msg) {
/*    */     int msgno;
/*    */     try {
/* 74 */       msgno = msg.getMessageNumber();
/* 75 */     } catch (Exception e) {
/* 76 */       return false;
/*    */     } 
/*    */     
/* 79 */     return match(msgno);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 86 */     if (!(obj instanceof MessageNumberTerm))
/* 87 */       return false; 
/* 88 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\MessageNumberTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */