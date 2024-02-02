/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.nio.channels.CancelledKeyException;
/*    */ import java.nio.channels.Channel;
/*    */ import java.nio.channels.SelectionKey;
/*    */ import org.xnio.Bits;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio.IoUtils;
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
/*    */ final class NioUdpChannelHandle
/*    */   extends NioHandle
/*    */ {
/*    */   private final NioUdpChannel channel;
/*    */   
/*    */   NioUdpChannelHandle(WorkerThread workerThread, SelectionKey selectionKey, NioUdpChannel channel) {
/* 34 */     super(workerThread, selectionKey);
/* 35 */     this.channel = channel;
/*    */   }
/*    */   
/*    */   void handleReady(int ops) {
/*    */     try {
/* 40 */       if (ops == 0) {
/*    */         
/* 42 */         SelectionKey key = getSelectionKey();
/* 43 */         int interestOps = key.interestOps();
/* 44 */         if (interestOps != 0) {
/* 45 */           ops = interestOps;
/*    */         } else {
/*    */           
/* 48 */           forceTermination();
/*    */           return;
/*    */         } 
/*    */       } 
/* 52 */       if (Bits.allAreSet(ops, 1)) {
/* 53 */         try { ChannelListeners.invokeChannelListener((Channel)this.channel, this.channel.getReadListener()); }
/* 54 */         catch (Throwable throwable) {}
/*    */       }
/* 56 */       if (Bits.allAreSet(ops, 4)) {
/* 57 */         try { ChannelListeners.invokeChannelListener((Channel)this.channel, this.channel.getWriteListener()); }
/* 58 */         catch (Throwable throwable) {}
/*    */       }
/* 60 */     } catch (CancelledKeyException cancelledKeyException) {}
/*    */   }
/*    */   
/*    */   void forceTermination() {
/* 64 */     IoUtils.safeClose((Closeable)this.channel);
/*    */   }
/*    */   
/*    */   void terminated() {
/* 68 */     this.channel.invokeCloseHandler();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioUdpChannelHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */