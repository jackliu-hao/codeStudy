/*    */ package org.xnio;
/*    */ 
/*    */ import java.nio.channels.Channel;
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
/*    */ public final class ChainedChannelListener<T extends Channel>
/*    */   implements ChannelListener<T>
/*    */ {
/*    */   private final ChannelListener<? super T>[] listeners;
/*    */   
/*    */   public ChainedChannelListener(ChannelListener<? super T>... listeners) {
/* 20 */     this.listeners = (ChannelListener<? super T>[])listeners.clone();
/*    */   }
/*    */   
/*    */   public void handleEvent(T channel) {
/* 24 */     for (ChannelListener<? super T> listener : this.listeners)
/* 25 */       ChannelListeners.invokeChannelListener(channel, listener); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChainedChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */