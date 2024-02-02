/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.channels.CloseListenerSettable;
/*    */ import org.xnio.channels.ReadListenerSettable;
/*    */ import org.xnio.channels.SuspendableReadChannel;
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
/*    */ public interface ReadReadyHandler
/*    */   extends TerminateHandler
/*    */ {
/*    */   void readReady();
/*    */   
/*    */   public static class ChannelListenerHandler<C extends SuspendableReadChannel & ReadListenerSettable<C> & CloseListenerSettable<C>>
/*    */     implements ReadReadyHandler
/*    */   {
/*    */     private final C channel;
/*    */     
/*    */     public ChannelListenerHandler(C channel) {
/* 54 */       this.channel = channel;
/*    */     }
/*    */     
/*    */     public void forceTermination() {
/* 58 */       IoUtils.safeClose((Closeable)this.channel);
/*    */     }
/*    */     
/*    */     public void readReady() {
/* 62 */       ChannelListener<? super C> readListener = ((ReadListenerSettable)this.channel).getReadListener();
/* 63 */       if (readListener == null) {
/* 64 */         this.channel.suspendReads();
/*    */       } else {
/* 66 */         ChannelListeners.invokeChannelListener((Channel)this.channel, readListener);
/*    */       } 
/*    */     }
/*    */     
/*    */     public void terminated() {
/* 71 */       ChannelListeners.invokeChannelListener((Channel)this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
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
/*    */     private final ReadReadyHandler handler;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public ReadyTask(ReadReadyHandler handler) {
/* 88 */       this.handler = handler;
/*    */     }
/*    */     
/*    */     public void run() {
/* 92 */       this.handler.readReady();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ReadReadyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */