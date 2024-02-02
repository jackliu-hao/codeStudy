/*    */ package io.undertow.protocols.ajp;
/*    */ 
/*    */ import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;
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
/*    */ public class AbstractAjpClientStreamSinkChannel
/*    */   extends AbstractFramedStreamSinkChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel>
/*    */ {
/*    */   protected AbstractAjpClientStreamSinkChannel(AjpClientChannel channel) {
/* 28 */     super(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isLastFrame() {
/* 33 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AbstractAjpClientStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */