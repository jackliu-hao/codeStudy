/*    */ package io.undertow.protocols.ajp;
/*    */ 
/*    */ import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;
/*    */ import io.undertow.server.protocol.framed.FramePriority;
/*    */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*    */ import java.util.Deque;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
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
/*    */ class AjpClientFramePriority
/*    */   implements FramePriority<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel>
/*    */ {
/* 34 */   public static AjpClientFramePriority INSTANCE = new AjpClientFramePriority();
/*    */ 
/*    */   
/*    */   public boolean insertFrame(AbstractAjpClientStreamSinkChannel newFrame, List<AbstractAjpClientStreamSinkChannel> pendingFrames) {
/* 38 */     if (newFrame instanceof AjpClientRequestClientStreamSinkChannel) {
/* 39 */       SendFrameHeader header = ((AjpClientRequestClientStreamSinkChannel)newFrame).generateSendFrameHeader();
/* 40 */       if (header.getByteBuffer() == null) {
/*    */         
/* 42 */         ((AjpClientRequestClientStreamSinkChannel)newFrame).clearHeader();
/* 43 */         return false;
/*    */       } 
/*    */     } 
/* 46 */     pendingFrames.add(newFrame);
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void frameAdded(AbstractAjpClientStreamSinkChannel addedFrame, List<AbstractAjpClientStreamSinkChannel> pendingFrames, Deque<AbstractAjpClientStreamSinkChannel> holdFrames) {
/* 52 */     Iterator<AbstractAjpClientStreamSinkChannel> it = holdFrames.iterator();
/* 53 */     while (it.hasNext()) {
/* 54 */       AbstractAjpClientStreamSinkChannel pending = it.next();
/* 55 */       if (pending instanceof AjpClientRequestClientStreamSinkChannel) {
/* 56 */         SendFrameHeader header = ((AjpClientRequestClientStreamSinkChannel)pending).generateSendFrameHeader();
/* 57 */         if (header.getByteBuffer() != null) {
/* 58 */           pendingFrames.add(pending);
/* 59 */           it.remove();
/*    */           continue;
/*    */         } 
/* 62 */         ((AjpClientRequestClientStreamSinkChannel)pending).clearHeader();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpClientFramePriority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */