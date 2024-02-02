/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Closer
/*    */   implements Runnable
/*    */ {
/*    */   private final Closeable resource;
/*    */   
/*    */   public Closer(Closeable resource) {
/* 15 */     this.resource = resource;
/*    */   }
/*    */   
/*    */   public void run() {
/* 19 */     IoUtils.safeClose(this.resource);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Closer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */