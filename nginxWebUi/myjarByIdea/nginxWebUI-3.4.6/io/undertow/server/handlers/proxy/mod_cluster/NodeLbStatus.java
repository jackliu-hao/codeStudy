package io.undertow.server.handlers.proxy.mod_cluster;

class NodeLbStatus {
   private volatile int oldelected;
   private volatile int lbfactor;
   private volatile int lbstatus;
   private volatile int elected;

   public int getLbFactor() {
      return this.lbfactor;
   }

   public int getElected() {
      return this.elected;
   }

   synchronized int getElectedDiff() {
      return this.elected - this.oldelected;
   }

   synchronized boolean update() {
      int elected = this.elected;
      int oldelected = this.oldelected;
      int lbfactor = this.lbfactor;
      if (lbfactor > 0) {
         this.lbstatus = (elected - oldelected) * 1000 / lbfactor;
      }

      this.oldelected = elected;
      return elected != oldelected;
   }

   synchronized void elected() {
      if (this.elected == Integer.MAX_VALUE) {
         this.oldelected = this.elected - this.oldelected;
         this.elected = 1;
      } else {
         ++this.elected;
      }

   }

   void updateLoad(int load) {
      this.lbfactor = load;
   }

   synchronized int getLbStatus() {
      int lbfactor = this.lbfactor;
      return lbfactor > 0 ? (this.elected - this.oldelected) * 1000 / lbfactor + this.lbstatus : -1;
   }
}
