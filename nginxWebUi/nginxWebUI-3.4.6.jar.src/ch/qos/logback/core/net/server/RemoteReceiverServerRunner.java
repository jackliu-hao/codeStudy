/*    */ package ch.qos.logback.core.net.server;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.concurrent.ArrayBlockingQueue;
/*    */ import java.util.concurrent.Executor;
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
/*    */ class RemoteReceiverServerRunner
/*    */   extends ConcurrentServerRunner<RemoteReceiverClient>
/*    */ {
/*    */   private final int clientQueueSize;
/*    */   
/*    */   public RemoteReceiverServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor, int clientQueueSize) {
/* 40 */     super(listener, executor);
/* 41 */     this.clientQueueSize = clientQueueSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean configureClient(RemoteReceiverClient client) {
/* 49 */     client.setContext(getContext());
/* 50 */     client.setQueue(new ArrayBlockingQueue<Serializable>(this.clientQueueSize));
/* 51 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\RemoteReceiverServerRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */