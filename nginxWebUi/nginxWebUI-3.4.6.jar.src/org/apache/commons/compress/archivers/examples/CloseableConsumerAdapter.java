/*    */ package org.apache.commons.compress.archivers.examples;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.util.Objects;
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
/*    */ final class CloseableConsumerAdapter
/*    */   implements Closeable
/*    */ {
/*    */   private final CloseableConsumer consumer;
/*    */   private Closeable closeable;
/*    */   
/*    */   CloseableConsumerAdapter(CloseableConsumer consumer) {
/* 30 */     this.consumer = Objects.<CloseableConsumer>requireNonNull(consumer, "consumer");
/*    */   }
/*    */   
/*    */   <C extends Closeable> C track(C closeable) {
/* 34 */     this.closeable = (Closeable)closeable;
/* 35 */     return closeable;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 40 */     if (this.closeable != null)
/* 41 */       this.consumer.accept(this.closeable); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\examples\CloseableConsumerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */