/*    */ package org.apache.http.impl.bootstrap;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ class ThreadFactoryImpl
/*    */   implements ThreadFactory
/*    */ {
/*    */   private final String namePrefix;
/*    */   private final ThreadGroup group;
/*    */   private final AtomicLong count;
/*    */   
/*    */   ThreadFactoryImpl(String namePrefix, ThreadGroup group) {
/* 42 */     this.namePrefix = namePrefix;
/* 43 */     this.group = group;
/* 44 */     this.count = new AtomicLong();
/*    */   }
/*    */   
/*    */   ThreadFactoryImpl(String namePrefix) {
/* 48 */     this(namePrefix, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public Thread newThread(Runnable target) {
/* 53 */     return new Thread(this.group, target, this.namePrefix + "-" + this.count.incrementAndGet());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\ThreadFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */