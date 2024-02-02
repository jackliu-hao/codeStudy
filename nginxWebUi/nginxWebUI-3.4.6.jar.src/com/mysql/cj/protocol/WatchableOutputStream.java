/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
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
/*    */ public class WatchableOutputStream
/*    */   extends ByteArrayOutputStream
/*    */   implements WatchableStream
/*    */ {
/*    */   private OutputStreamWatcher watcher;
/*    */   
/*    */   public void close() throws IOException {
/* 46 */     super.close();
/*    */     
/* 48 */     if (this.watcher != null) {
/* 49 */       this.watcher.streamClosed(this);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setWatcher(OutputStreamWatcher watcher) {
/* 54 */     this.watcher = watcher;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\WatchableOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */