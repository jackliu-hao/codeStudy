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
/*    */ public interface ReadListenerSettable<C extends Channel>
/*    */ {
/*    */   void setReadListener(ChannelListener<? super C> paramChannelListener);
/*    */   
/*    */   ChannelListener<? super C> getReadListener();
/*    */   
/*    */   public static class Setter<C extends Channel>
/*    */     implements ChannelListener.Setter<C>
/*    */   {
/*    */     private final ReadListenerSettable<C> settable;
/*    */     
/*    */     public Setter(ReadListenerSettable<C> settable) {
/* 58 */       this.settable = settable;
/*    */     }
/*    */     
/*    */     public void set(ChannelListener<? super C> listener) {
/* 62 */       this.settable.setReadListener(listener);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ReadListenerSettable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */