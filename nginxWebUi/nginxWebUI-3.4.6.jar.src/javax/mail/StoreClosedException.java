/*    */ package javax.mail;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StoreClosedException
/*    */   extends MessagingException
/*    */ {
/*    */   private transient Store store;
/*    */   private static final long serialVersionUID = -3145392336120082655L;
/*    */   
/*    */   public StoreClosedException(Store store) {
/* 68 */     this(store, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StoreClosedException(Store store, String message) {
/* 77 */     super(message);
/* 78 */     this.store = store;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Store getStore() {
/* 85 */     return this.store;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\StoreClosedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */