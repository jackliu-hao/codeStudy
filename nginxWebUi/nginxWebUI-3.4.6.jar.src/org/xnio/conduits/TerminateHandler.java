/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.channels.CloseListenerSettable;
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
/*    */ public interface TerminateHandler
/*    */ {
/*    */   void forceTermination();
/*    */   
/*    */   void terminated();
/*    */   
/*    */   public static class ChannelListenerHandler<C extends Channel & CloseListenerSettable<C>>
/*    */     implements TerminateHandler
/*    */   {
/*    */     private final C channel;
/*    */     
/*    */     public ChannelListenerHandler(C channel) {
/* 58 */       this.channel = channel;
/*    */     }
/*    */     
/*    */     public void forceTermination() {
/* 62 */       IoUtils.safeClose((Closeable)this.channel);
/*    */     }
/*    */     
/*    */     public void terminated() {
/* 66 */       ChannelListeners.invokeChannelListener((Channel)this.channel, ((CloseListenerSettable)this.channel).getCloseListener());
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
/*    */     private final TerminateHandler handler;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public ReadyTask(TerminateHandler handler) {
/* 83 */       this.handler = handler;
/*    */     }
/*    */     
/*    */     public void run() {
/* 87 */       this.handler.terminated();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\TerminateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */