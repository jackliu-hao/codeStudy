/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SweeperPool
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private transient Sweeper sweeper;
/*     */   private transient int maxSize;
/*     */   private transient int minSize;
/*     */   private int triggerSize;
/*     */   private ArrayList pooledObjects;
/*     */   private boolean shuttingDown = false;
/*     */   
/*     */   public SweeperPool(int maxSize, int minSize, int intialCapacity, int sweepInterval, int triggerSize) {
/*  79 */     this.maxSize = saneConvert(maxSize);
/*  80 */     this.minSize = saneConvert(minSize);
/*  81 */     this.triggerSize = saneConvert(triggerSize);
/*  82 */     this.pooledObjects = new ArrayList(intialCapacity);
/*     */ 
/*     */     
/*  85 */     if (sweepInterval > 0) {
/*     */       
/*  87 */       this.sweeper = new Sweeper(this, sweepInterval);
/*  88 */       this.sweeper.start();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private int saneConvert(int value) {
/*  94 */     if (value < 0)
/*     */     {
/*  96 */       return 0;
/*     */     }
/*     */ 
/*     */     
/* 100 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Object get() {
/* 109 */     if (this.pooledObjects.size() == 0 || this.shuttingDown)
/*     */     {
/* 111 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 115 */     Object obj = this.pooledObjects.remove(0);
/* 116 */     objectRetrieved(obj);
/*     */ 
/*     */     
/* 119 */     return obj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean put(Object obj) {
/* 133 */     objectAdded(obj);
/*     */     
/* 135 */     if (obj != null && this.pooledObjects.size() < this.maxSize && !this.shuttingDown) {
/*     */ 
/*     */       
/* 138 */       this.pooledObjects.add(obj);
/*     */       
/* 140 */       return true;
/*     */     } 
/* 142 */     if (obj != null)
/*     */     {
/*     */       
/* 145 */       objectDisposed(obj);
/*     */     }
/*     */     
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int getSize() {
/* 159 */     return this.pooledObjects.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 168 */     this.shuttingDown = true;
/*     */     
/* 170 */     if (this.sweeper != null) {
/*     */       
/* 172 */       this.sweeper.stop();
/*     */       
/*     */       try {
/* 175 */         this.sweeper.join();
/*     */       }
/* 177 */       catch (InterruptedException e) {
/*     */         
/* 179 */         System.err.println("Unexpected execption occurred: ");
/* 180 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/* 184 */     synchronized (this) {
/*     */ 
/*     */ 
/*     */       
/* 188 */       Object[] objects = this.pooledObjects.toArray();
/*     */       
/* 190 */       for (int i = 0; i < objects.length; i++)
/*     */       {
/* 192 */         objectDisposed(objects[i]);
/*     */       }
/*     */       
/* 195 */       this.pooledObjects.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isDisposed() {
/* 206 */     if (!this.shuttingDown)
/*     */     {
/* 208 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 212 */     if (this.sweeper == null)
/*     */     {
/* 214 */       return true;
/*     */     }
/*     */     
/* 217 */     return this.sweeper.hasStopped();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void trim() {
/* 226 */     if ((this.triggerSize > 0 && this.pooledObjects.size() >= this.triggerSize) || (this.maxSize > 0 && this.pooledObjects.size() >= this.maxSize))
/*     */     {
/*     */       
/* 229 */       while (this.pooledObjects.size() > this.minSize)
/*     */       {
/* 231 */         objectDisposed(this.pooledObjects.remove(0));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void objectDisposed(Object obj) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void objectAdded(Object obj) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void objectRetrieved(Object obj) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Sweeper
/*     */     implements Runnable
/*     */   {
/*     */     private final transient SweeperPool pool;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private transient boolean service = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final transient int sweepInterval;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 280 */     private transient Thread t = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Sweeper(SweeperPool pool, int sweepInterval) {
/* 288 */       this.sweepInterval = sweepInterval;
/* 289 */       this.pool = pool;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 299 */       debug("started");
/*     */       
/* 301 */       if (this.sweepInterval > 0)
/*     */       {
/* 303 */         synchronized (this) {
/*     */           
/* 305 */           while (this.service) {
/*     */ 
/*     */             
/*     */             try {
/*     */ 
/*     */               
/* 311 */               wait((this.sweepInterval * 1000));
/*     */             }
/* 313 */             catch (InterruptedException e) {}
/*     */ 
/*     */             
/* 316 */             runSweep();
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 321 */       debug("stopped");
/*     */     }
/*     */ 
/*     */     
/*     */     public void start() {
/* 326 */       if (!this.service) {
/*     */         
/* 328 */         this.service = true;
/* 329 */         this.t = new Thread(this);
/* 330 */         this.t.setName("Sweeper");
/* 331 */         this.t.start();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized void stop() {
/* 337 */       this.service = false;
/* 338 */       notifyAll();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void join() throws InterruptedException {
/* 344 */       this.t.join();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasStopped() {
/* 349 */       return (!this.service && !this.t.isAlive());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final void debug(String msg) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void runSweep() {
/* 362 */       debug("runningSweep. time=" + System.currentTimeMillis());
/* 363 */       this.pool.trim();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\SweeperPool.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */