/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.ChannelListeners;
/*    */ import org.xnio.XnioIoThread;
/*    */ import org.xnio.XnioWorker;
/*    */ import org.xnio.channels.CloseableChannel;
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
/*    */ abstract class AbstractNioChannel<C extends AbstractNioChannel<C>>
/*    */   implements CloseableChannel
/*    */ {
/*    */   protected static final int DEFAULT_BUFFER_SIZE = 65536;
/* 31 */   private final ChannelListener.SimpleSetter<C> closeSetter = new ChannelListener.SimpleSetter();
/*    */   
/*    */   protected final NioXnioWorker worker;
/*    */   
/*    */   AbstractNioChannel(NioXnioWorker worker) {
/* 36 */     this.worker = worker;
/*    */   }
/*    */   
/*    */   public final NioXnioWorker getWorker() {
/* 40 */     return this.worker;
/*    */   }
/*    */   
/*    */   public final ChannelListener.Setter<? extends C> getCloseSetter() {
/* 44 */     return (ChannelListener.Setter<? extends C>)this.closeSetter;
/*    */   }
/*    */   
/*    */   public XnioIoThread getIoThread() {
/* 48 */     return this.worker.chooseThread();
/*    */   }
/*    */ 
/*    */   
/*    */   protected final C typed() {
/* 53 */     return (C)this;
/*    */   }
/*    */   
/*    */   protected final void invokeCloseHandler() {
/* 57 */     ChannelListeners.invokeChannelListener((Channel)typed(), this.closeSetter.get());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\AbstractNioChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */