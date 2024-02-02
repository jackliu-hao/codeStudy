/*    */ package javax.mail.search;
/*    */ 
/*    */ import javax.mail.Address;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AddressTerm
/*    */   extends SearchTerm
/*    */ {
/*    */   protected Address address;
/*    */   private static final long serialVersionUID = 2005405551929769980L;
/*    */   
/*    */   protected AddressTerm(Address address) {
/* 63 */     this.address = address;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Address getAddress() {
/* 70 */     return this.address;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean match(Address a) {
/* 77 */     return a.equals(this.address);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 84 */     if (!(obj instanceof AddressTerm))
/* 85 */       return false; 
/* 86 */     AddressTerm at = (AddressTerm)obj;
/* 87 */     return at.address.equals(this.address);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 94 */     return this.address.hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\AddressTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */