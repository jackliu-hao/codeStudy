/*    */ package org.noear.solon.ext;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import org.noear.solon.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/*    */   private final String namePrefix;
/* 16 */   private final AtomicInteger threadCount = new AtomicInteger(0);
/*    */   
/*    */   private ThreadGroup group;
/*    */   private boolean daemon = false;
/* 20 */   private int priority = 5;
/*    */   
/*    */   public NamedThreadFactory(String namePrefix) {
/* 23 */     if (Utils.isEmpty(namePrefix)) {
/* 24 */       this.namePrefix = getClass().getSimpleName() + "-";
/*    */     } else {
/* 26 */       this.namePrefix = namePrefix;
/*    */     } 
/*    */   }
/*    */   
/*    */   public NamedThreadFactory group(ThreadGroup group) {
/* 31 */     this.group = group;
/* 32 */     return this;
/*    */   }
/*    */   
/*    */   public NamedThreadFactory daemon(boolean daemon) {
/* 36 */     this.daemon = daemon;
/* 37 */     return this;
/*    */   }
/*    */   
/*    */   public NamedThreadFactory priority(int priority) {
/* 41 */     this.priority = priority;
/* 42 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Thread newThread(Runnable r) {
/* 47 */     Thread t = new Thread(this.group, r, this.namePrefix + this.threadCount.incrementAndGet());
/* 48 */     t.setDaemon(this.daemon);
/* 49 */     t.setPriority(this.priority);
/*    */     
/* 51 */     return t;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\ext\NamedThreadFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */