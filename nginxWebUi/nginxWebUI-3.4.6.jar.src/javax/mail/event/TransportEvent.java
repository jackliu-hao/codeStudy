/*     */ package javax.mail.event;
/*     */ 
/*     */ import javax.mail.Address;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.Transport;
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
/*     */ public class TransportEvent
/*     */   extends MailEvent
/*     */ {
/*     */   public static final int MESSAGE_DELIVERED = 1;
/*     */   public static final int MESSAGE_NOT_DELIVERED = 2;
/*     */   public static final int MESSAGE_PARTIALLY_DELIVERED = 3;
/*     */   protected int type;
/*     */   protected transient Address[] validSent;
/*     */   protected transient Address[] validUnsent;
/*     */   protected transient Address[] invalid;
/*     */   protected transient Message msg;
/*     */   private static final long serialVersionUID = -4729852364684273073L;
/*     */   
/*     */   public TransportEvent(Transport transport, int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
/* 103 */     super(transport);
/* 104 */     this.type = type;
/* 105 */     this.validSent = validSent;
/* 106 */     this.validUnsent = validUnsent;
/* 107 */     this.invalid = invalid;
/* 108 */     this.msg = msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 116 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getValidSentAddresses() {
/* 124 */     return this.validSent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getValidUnsentAddresses() {
/* 134 */     return this.validUnsent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Address[] getInvalidAddresses() {
/* 142 */     return this.invalid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Message getMessage() {
/* 152 */     return this.msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(Object listener) {
/* 159 */     if (this.type == 1) {
/* 160 */       ((TransportListener)listener).messageDelivered(this);
/* 161 */     } else if (this.type == 2) {
/* 162 */       ((TransportListener)listener).messageNotDelivered(this);
/*     */     } else {
/* 164 */       ((TransportListener)listener).messagePartiallyDelivered(this);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\TransportEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */