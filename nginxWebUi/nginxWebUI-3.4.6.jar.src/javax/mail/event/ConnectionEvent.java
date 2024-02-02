/*    */ package javax.mail.event;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConnectionEvent
/*    */   extends MailEvent
/*    */ {
/*    */   public static final int OPENED = 1;
/*    */   public static final int DISCONNECTED = 2;
/*    */   public static final int CLOSED = 3;
/*    */   protected int type;
/*    */   private static final long serialVersionUID = -1855480171284792957L;
/*    */   
/*    */   public ConnectionEvent(Object source, int type) {
/* 75 */     super(source);
/* 76 */     this.type = type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getType() {
/* 84 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void dispatch(Object listener) {
/* 91 */     if (this.type == 1) {
/* 92 */       ((ConnectionListener)listener).opened(this);
/* 93 */     } else if (this.type == 2) {
/* 94 */       ((ConnectionListener)listener).disconnected(this);
/* 95 */     } else if (this.type == 3) {
/* 96 */       ((ConnectionListener)listener).closed(this);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\ConnectionEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */