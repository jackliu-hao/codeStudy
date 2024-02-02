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
/*    */ public interface CloseListenerSettable<C extends Channel>
/*    */ {
/*    */   void setCloseListener(ChannelListener<? super C> paramChannelListener);
/*    */   
/*    */   ChannelListener<? super C> getCloseListener();
/*    */   
/*    */   public static class Setter<C extends Channel>
/*    */     implements ChannelListener.Setter<C>
/*    */   {
/*    */     private final CloseListenerSettable<C> settable;
/*    */     
/*    */     public Setter(CloseListenerSettable<C> settable) {
/* 58 */       this.settable = settable;
/*    */     }
/*    */     
/*    */     public void set(ChannelListener<? super C> listener) {
/* 62 */       this.settable.setCloseListener(listener);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\CloseListenerSettable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */