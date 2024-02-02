/*    */ package org.xnio.nio;
/*    */ 
/*    */ import org.xnio.StreamConnection;
/*    */ import org.xnio.conduits.StreamSinkConduit;
/*    */ import org.xnio.conduits.StreamSourceConduit;
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
/*    */ abstract class AbstractNioStreamConnection
/*    */   extends StreamConnection
/*    */ {
/*    */   protected AbstractNioStreamConnection(WorkerThread workerThread) {
/* 31 */     super(workerThread);
/*    */   }
/*    */   
/*    */   protected void setSourceConduit(StreamSourceConduit conduit) {
/* 35 */     super.setSourceConduit(conduit);
/*    */   }
/*    */   
/*    */   protected void setSinkConduit(StreamSinkConduit conduit) {
/* 39 */     super.setSinkConduit(conduit);
/*    */   }
/*    */   
/*    */   protected boolean readClosed() {
/* 43 */     return super.readClosed();
/*    */   }
/*    */   
/*    */   protected boolean writeClosed() {
/* 47 */     return super.writeClosed();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\AbstractNioStreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */