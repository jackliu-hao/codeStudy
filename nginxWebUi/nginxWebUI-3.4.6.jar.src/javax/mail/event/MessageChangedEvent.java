/*     */ package javax.mail.event;
/*     */ 
/*     */ import javax.mail.Message;
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
/*     */ public class MessageChangedEvent
/*     */   extends MailEvent
/*     */ {
/*     */   public static final int FLAGS_CHANGED = 1;
/*     */   public static final int ENVELOPE_CHANGED = 2;
/*     */   protected int type;
/*     */   protected transient Message msg;
/*     */   private static final long serialVersionUID = -4974972972105535108L;
/*     */   
/*     */   public MessageChangedEvent(Object source, int type, Message msg) {
/*  80 */     super(source);
/*  81 */     this.msg = msg;
/*  82 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMessageChangeType() {
/*  90 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message getMessage() {
/*  98 */     return this.msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(Object listener) {
/* 105 */     ((MessageChangedListener)listener).messageChanged(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\MessageChangedEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */