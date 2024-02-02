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
/*    */ public interface AcceptListenerSettable<C extends Channel>
/*    */ {
/*    */   ChannelListener<? super C> getAcceptListener();
/*    */   
/*    */   void setAcceptListener(ChannelListener<? super C> paramChannelListener);
/*    */   
/*    */   public static class Setter<C extends Channel>
/*    */     implements ChannelListener.Setter<C>
/*    */   {
/*    */     private final AcceptListenerSettable<C> settable;
/*    */     
/*    */     public Setter(AcceptListenerSettable<C> settable) {
/* 58 */       this.settable = settable;
/*    */     }
/*    */     
/*    */     public void set(ChannelListener<? super C> listener) {
/* 62 */       this.settable.setAcceptListener(listener);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AcceptListenerSettable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */