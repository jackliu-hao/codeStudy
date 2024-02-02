/*     */ package javax.mail.event;
/*     */ 
/*     */ import javax.mail.Store;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StoreEvent
/*     */   extends MailEvent
/*     */ {
/*     */   public static final int ALERT = 1;
/*     */   public static final int NOTICE = 2;
/*     */   protected int type;
/*     */   protected String message;
/*     */   private static final long serialVersionUID = 1938704919992515330L;
/*     */   
/*     */   public StoreEvent(Store store, int type, String message) {
/*  88 */     super(store);
/*  89 */     this.type = type;
/*  90 */     this.message = message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMessageType() {
/* 101 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 110 */     return this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(Object listener) {
/* 117 */     ((StoreListener)listener).notification(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\StoreEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */