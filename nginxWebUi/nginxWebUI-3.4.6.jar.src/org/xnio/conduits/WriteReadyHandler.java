/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.channels.CloseListenerSettable;
/*    */ import org.xnio.channels.SuspendableWriteChannel;
/*    */ import org.xnio.channels.WriteListenerSettable;
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
/*    */ public interface WriteReadyHandler
/*    */   extends TerminateHandler
/*    */ {
/*    */   void writeReady();
/*    */   
/*    */   public static class ChannelListenerHandler<C extends SuspendableWriteChannel & WriteListenerSettable<C> & CloseListenerSettable<C>>
/*    */     implements WriteReadyHandler
/*    */   {
/*    */     private final C channel;
/*    */     
/*    */     public ChannelListenerHandler(C channel) {
/* 53 */       this.channel = channel;
/*    */     }
/*    */     
/*    */     public void forceTermination() {
/* 57 */       IoUtils.safeClose((Closeable)this.channel);
/*    */     }
/*    */     
/*    */     public void writeReady() {
/* 61 */       ChannelListener<? super C> writeListener = ((WriteListenerSettable)this.channel).getWriteListener();
/* 62 */       if (writeListener == null) {
/* 63 */         this.channel.suspendWrites();
/*    */       } else {
/* 65 */         ChannelListeners.invokeChannelListener((Channel)this.channel, writeListener);
/*    */       } 
/*    */     }
/*    */     
/*    */     public void terminated() {
/* 70 */       ChannelListeners.invokeChannelListener((Channel)this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static class ReadyTask
/*    */     implements Runnable
/*    */   {
/*    */     private final WriteReadyHandler handler;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public ReadyTask(WriteReadyHandler handler) {
/* 87 */       this.handler = handler;
/*    */     }
/*    */     
/*    */     public void run() {
/* 91 */       this.handler.writeReady();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\WriteReadyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */