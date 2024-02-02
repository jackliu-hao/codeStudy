/*    */ package javax.mail.search;
/*    */ 
/*    */ import java.util.Date;
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
/*    */ public final class ReceivedDateTerm
/*    */   extends DateTerm
/*    */ {
/*    */   private static final long serialVersionUID = -2756695246195503170L;
/*    */   
/*    */   public ReceivedDateTerm(int comparison, Date date) {
/* 63 */     super(comparison, date);
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
/*    */     Date d;
/*    */     try {
/* 77 */       d = msg.getReceivedDate();
/* 78 */     } catch (Exception e) {
/* 79 */       return false;
/*    */     } 
/*    */     
/* 82 */     if (d == null) {
/* 83 */       return false;
/*    */     }
/* 85 */     return match(d);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 92 */     if (!(obj instanceof ReceivedDateTerm))
/* 93 */       return false; 
/* 94 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\ReceivedDateTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */