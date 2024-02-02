/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
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
/*    */ public class LocalPortGenerater
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final AtomicInteger alternativePort;
/*    */   
/*    */   public LocalPortGenerater(int beginPort) {
/* 27 */     this.alternativePort = new AtomicInteger(beginPort);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int generate() {
/* 36 */     int validPort = this.alternativePort.get();
/*    */     
/* 38 */     while (false == NetUtil.isUsableLocalPort(validPort)) {
/* 39 */       validPort = this.alternativePort.incrementAndGet();
/*    */     }
/* 41 */     return validPort;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\LocalPortGenerater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */