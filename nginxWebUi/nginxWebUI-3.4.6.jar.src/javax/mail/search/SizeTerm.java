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
/*    */ public final class SizeTerm
/*    */   extends IntegerComparisonTerm
/*    */ {
/*    */   private static final long serialVersionUID = -2556219451005103709L;
/*    */   
/*    */   public SizeTerm(int comparison, int size) {
/* 62 */     super(comparison, size);
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
/*    */     int size;
/*    */     try {
/* 75 */       size = msg.getSize();
/* 76 */     } catch (Exception e) {
/* 77 */       return false;
/*    */     } 
/*    */     
/* 80 */     if (size == -1) {
/* 81 */       return false;
/*    */     }
/* 83 */     return match(size);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 90 */     if (!(obj instanceof SizeTerm))
/* 91 */       return false; 
/* 92 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\SizeTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */