/*    */ package com.sun.jna;
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
/*    */ public class CallbackThreadInitializer
/*    */ {
/*    */   private boolean daemon;
/*    */   private boolean detach;
/*    */   private String name;
/*    */   private ThreadGroup group;
/*    */   
/*    */   public CallbackThreadInitializer() {
/* 54 */     this(true);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public CallbackThreadInitializer(boolean daemon) {
/* 60 */     this(daemon, false);
/*    */   }
/*    */   
/*    */   public CallbackThreadInitializer(boolean daemon, boolean detach) {
/* 64 */     this(daemon, detach, null);
/*    */   }
/*    */   
/*    */   public CallbackThreadInitializer(boolean daemon, boolean detach, String name) {
/* 68 */     this(daemon, detach, name, null);
/*    */   }
/*    */   
/*    */   public CallbackThreadInitializer(boolean daemon, boolean detach, String name, ThreadGroup group) {
/* 72 */     this.daemon = daemon;
/* 73 */     this.detach = detach;
/* 74 */     this.name = name;
/* 75 */     this.group = group;
/*    */   }
/*    */   
/*    */   public String getName(Callback cb) {
/* 79 */     return this.name;
/*    */   } public ThreadGroup getThreadGroup(Callback cb) {
/* 81 */     return this.group;
/*    */   } public boolean isDaemon(Callback cb) {
/* 83 */     return this.daemon;
/*    */   }
/*    */   
/*    */   public boolean detach(Callback cb) {
/* 87 */     return this.detach;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\CallbackThreadInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */