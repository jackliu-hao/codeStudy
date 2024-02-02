/*     */ package javax.mail;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface UIDFolder
/*     */ {
/*     */   public static final long LASTUID = -1L;
/*     */   
/*     */   long getUIDValidity() throws MessagingException;
/*     */   
/*     */   Message getMessageByUID(long paramLong) throws MessagingException;
/*     */   
/*     */   Message[] getMessagesByUID(long paramLong1, long paramLong2) throws MessagingException;
/*     */   
/*     */   Message[] getMessagesByUID(long[] paramArrayOflong) throws MessagingException;
/*     */   
/*     */   long getUID(Message paramMessage) throws MessagingException;
/*     */   
/*     */   public static class FetchProfileItem
/*     */     extends FetchProfile.Item
/*     */   {
/*     */     protected FetchProfileItem(String name) {
/*  93 */       super(name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     public static final FetchProfileItem UID = new FetchProfileItem("UID");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\UIDFolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */