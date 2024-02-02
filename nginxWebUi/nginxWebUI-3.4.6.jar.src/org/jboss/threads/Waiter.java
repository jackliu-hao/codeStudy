/*    */ package org.jboss.threads;
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
/*    */ final class Waiter
/*    */ {
/*    */   private volatile Thread thread;
/*    */   private Waiter next;
/*    */   
/*    */   Waiter(Waiter next) {
/* 28 */     this.next = next;
/*    */   }
/*    */   
/*    */   Thread getThread() {
/* 32 */     return this.thread;
/*    */   }
/*    */   
/*    */   Waiter setThread(Thread thread) {
/* 36 */     this.thread = thread;
/* 37 */     return this;
/*    */   }
/*    */   
/*    */   Waiter getNext() {
/* 41 */     return this.next;
/*    */   }
/*    */   
/*    */   Waiter setNext(Waiter next) {
/* 45 */     this.next = next;
/* 46 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\Waiter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */