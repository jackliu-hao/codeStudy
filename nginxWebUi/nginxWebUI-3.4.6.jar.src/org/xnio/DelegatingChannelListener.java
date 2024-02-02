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
/*    */ public abstract class DelegatingChannelListener<T extends Channel>
/*    */   implements ChannelListener<T>
/*    */ {
/*    */   private final ChannelListener<? super T> next;
/*    */   
/*    */   protected DelegatingChannelListener(ChannelListener<? super T> next) {
/* 39 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void callNext(T channel) {
/* 48 */     ChannelListeners.invokeChannelListener(channel, this.next);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\DelegatingChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */