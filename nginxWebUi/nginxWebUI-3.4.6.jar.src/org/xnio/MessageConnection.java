/*    */ package org.xnio;
/*    */ 
/*    */ import org.xnio._private.Messages;
/*    */ import org.xnio.channels.CloseListenerSettable;
/*    */ import org.xnio.channels.Configurable;
/*    */ import org.xnio.conduits.ConduitReadableMessageChannel;
/*    */ import org.xnio.conduits.ConduitWritableMessageChannel;
/*    */ import org.xnio.conduits.MessageSinkConduit;
/*    */ import org.xnio.conduits.MessageSourceConduit;
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
/*    */ public abstract class MessageConnection
/*    */   extends Connection
/*    */   implements CloseListenerSettable<MessageConnection>
/*    */ {
/*    */   private ConduitReadableMessageChannel sourceChannel;
/*    */   private ConduitWritableMessageChannel sinkChannel;
/*    */   private ChannelListener<? super MessageConnection> closeListener;
/*    */   
/*    */   protected MessageConnection(XnioIoThread thread) {
/* 46 */     super(thread);
/*    */   }
/*    */   
/*    */   public void setCloseListener(ChannelListener<? super MessageConnection> listener) {
/* 50 */     this.closeListener = listener;
/*    */   }
/*    */   
/*    */   public ChannelListener<? super MessageConnection> getCloseListener() {
/* 54 */     return this.closeListener;
/*    */   }
/*    */   
/*    */   public ChannelListener.Setter<MessageConnection> getCloseSetter() {
/* 58 */     return (ChannelListener.Setter<MessageConnection>)new CloseListenerSettable.Setter(this);
/*    */   }
/*    */   
/*    */   protected void setSourceConduit(MessageSourceConduit conduit) {
/* 62 */     this.sourceChannel = (conduit == null) ? null : new ConduitReadableMessageChannel((Configurable)this, conduit);
/*    */   }
/*    */   
/*    */   protected void setSinkConduit(MessageSinkConduit conduit) {
/* 66 */     this.sinkChannel = (conduit == null) ? null : new ConduitWritableMessageChannel((Configurable)this, conduit);
/*    */   }
/*    */   
/*    */   void invokeCloseListener() {
/* 70 */     ChannelListeners.invokeChannelListener(this, this.closeListener);
/*    */   }
/*    */   
/*    */   private static <T> T notNull(T orig) throws IllegalStateException {
/* 74 */     if (orig == null) {
/* 75 */       throw Messages.msg.channelNotAvailable();
/*    */     }
/* 77 */     return orig;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConduitReadableMessageChannel getSourceChannel() {
/* 86 */     return notNull(this.sourceChannel);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConduitWritableMessageChannel getSinkChannel() {
/* 95 */     return notNull(this.sinkChannel);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\MessageConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */