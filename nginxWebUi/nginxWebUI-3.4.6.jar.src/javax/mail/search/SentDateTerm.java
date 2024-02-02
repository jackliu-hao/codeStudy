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
/*    */ public final class SentDateTerm
/*    */   extends DateTerm
/*    */ {
/*    */   private static final long serialVersionUID = 5647755030530907263L;
/*    */   
/*    */   public SentDateTerm(int comparison, Date date) {
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
/* 77 */       d = msg.getSentDate();
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
/* 92 */     if (!(obj instanceof SentDateTerm))
/* 93 */       return false; 
/* 94 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\SentDateTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */