/*    */ package org.xnio;
/*    */ 
/*    */ import java.nio.channels.Channel;
/*    */ import java.util.EventListener;
/*    */ import org.jboss.logging.Logger;
/*    */ import org.xnio._private.Messages;
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
/*    */ public interface ChannelListener<T extends Channel>
/*    */   extends EventListener
/*    */ {
/*    */   void handleEvent(T paramT);
/*    */   
/*    */   public static class SimpleSetter<T extends Channel>
/*    */     implements Setter<T>
/*    */   {
/*    */     private ChannelListener<? super T> channelListener;
/*    */     
/*    */     public void set(ChannelListener<? super T> listener) {
/* 75 */       Messages.listenerMsg.logf(SimpleSetter.class.getName(), Logger.Level.TRACE, null, "Setting channel listener to %s", listener);
/* 76 */       this.channelListener = listener;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public ChannelListener<? super T> get() {
/* 85 */       return this.channelListener;
/*    */     }
/*    */     
/*    */     public String toString() {
/* 89 */       return "Simple channel listener setter (currently=" + this.channelListener + ")";
/*    */     }
/*    */   }
/*    */   
/*    */   public static interface Setter<T extends Channel> {
/*    */     void set(ChannelListener<? super T> param1ChannelListener);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */