/*    */ package javax.mail.search;
/*    */ 
/*    */ import javax.mail.Address;
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
/*    */ public final class FromTerm
/*    */   extends AddressTerm
/*    */ {
/*    */   private static final long serialVersionUID = 5214730291502658665L;
/*    */   
/*    */   public FromTerm(Address address) {
/* 61 */     super(address);
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
/*    */     Address[] from;
/*    */     try {
/* 74 */       from = msg.getFrom();
/* 75 */     } catch (Exception e) {
/* 76 */       return false;
/*    */     } 
/*    */     
/* 79 */     if (from == null) {
/* 80 */       return false;
/*    */     }
/* 82 */     for (int i = 0; i < from.length; i++) {
/* 83 */       if (match(from[i]))
/* 84 */         return true; 
/* 85 */     }  return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 92 */     if (!(obj instanceof FromTerm))
/* 93 */       return false; 
/* 94 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\FromTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */