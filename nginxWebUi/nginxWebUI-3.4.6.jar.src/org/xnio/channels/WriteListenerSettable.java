/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.nio.channels.Channel;
/*    */ import org.xnio.ChannelListener;
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
/*    */ public interface WriteListenerSettable<C extends Channel>
/*    */ {
/*    */   void setWriteListener(ChannelListener<? super C> paramChannelListener);
/*    */   
/*    */   ChannelListener<? super C> getWriteListener();
/*    */   
/*    */   public static class Setter<C extends Channel>
/*    */     implements ChannelListener.Setter<C>
/*    */   {
/*    */     private final WriteListenerSettable<C> settable;
/*    */     
/*    */     public Setter(WriteListenerSettable<C> settable) {
/* 58 */       this.settable = settable;
/*    */     }
/*    */     
/*    */     public void set(ChannelListener<? super C> listener) {
/* 62 */       this.settable.setWriteListener(listener);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\WriteListenerSettable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */