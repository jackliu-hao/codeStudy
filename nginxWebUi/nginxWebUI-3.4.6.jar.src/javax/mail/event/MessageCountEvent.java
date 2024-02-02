/*     */ package javax.mail.event;
/*     */ 
/*     */ import javax.mail.Folder;
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
/*     */ public class MessageCountEvent
/*     */   extends MailEvent
/*     */ {
/*     */   public static final int ADDED = 1;
/*     */   public static final int REMOVED = 2;
/*     */   protected int type;
/*     */   protected boolean removed;
/*     */   protected transient Message[] msgs;
/*     */   private static final long serialVersionUID = -7447022340837897369L;
/*     */   
/*     */   public MessageCountEvent(Folder folder, int type, boolean removed, Message[] msgs) {
/* 108 */     super(folder);
/* 109 */     this.type = type;
/* 110 */     this.removed = removed;
/* 111 */     this.msgs = msgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 119 */     return this.type;
/*     */   }
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
/*     */   public boolean isRemoved() {
/* 134 */     return this.removed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message[] getMessages() {
/* 142 */     return this.msgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(Object listener) {
/* 149 */     if (this.type == 1) {
/* 150 */       ((MessageCountListener)listener).messagesAdded(this);
/*     */     } else {
/* 152 */       ((MessageCountListener)listener).messagesRemoved(this);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\MessageCountEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */