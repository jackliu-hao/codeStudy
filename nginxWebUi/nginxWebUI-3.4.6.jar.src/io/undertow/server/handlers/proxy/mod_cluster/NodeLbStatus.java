/*    */ package io.undertow.server.handlers.proxy.mod_cluster;
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
/*    */ class NodeLbStatus
/*    */ {
/*    */   private volatile int oldelected;
/*    */   private volatile int lbfactor;
/*    */   private volatile int lbstatus;
/*    */   private volatile int elected;
/*    */   
/*    */   public int getLbFactor() {
/* 35 */     return this.lbfactor;
/*    */   }
/*    */   
/*    */   public int getElected() {
/* 39 */     return this.elected;
/*    */   }
/*    */   
/*    */   synchronized int getElectedDiff() {
/* 43 */     return this.elected - this.oldelected;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   synchronized boolean update() {
/* 52 */     int elected = this.elected;
/* 53 */     int oldelected = this.oldelected;
/* 54 */     int lbfactor = this.lbfactor;
/* 55 */     if (lbfactor > 0) {
/* 56 */       this.lbstatus = (elected - oldelected) * 1000 / lbfactor;
/*    */     }
/* 58 */     this.oldelected = elected;
/* 59 */     return (elected != oldelected);
/*    */   }
/*    */   
/*    */   synchronized void elected() {
/* 63 */     if (this.elected == Integer.MAX_VALUE) {
/* 64 */       this.oldelected = this.elected - this.oldelected;
/* 65 */       this.elected = 1;
/*    */     } else {
/* 67 */       this.elected++;
/*    */     } 
/*    */   }
/*    */   
/*    */   void updateLoad(int load) {
/* 72 */     this.lbfactor = load;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   synchronized int getLbStatus() {
/* 81 */     int lbfactor = this.lbfactor;
/* 82 */     if (lbfactor > 0) {
/* 83 */       return (this.elected - this.oldelected) * 1000 / lbfactor + this.lbstatus;
/*    */     }
/* 85 */     return -1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\NodeLbStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */