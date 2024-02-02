package org.codehaus.plexus.util;

import java.util.ArrayList;

public class SweeperPool {
   private static final boolean DEBUG = false;
   private transient Sweeper sweeper;
   private transient int maxSize;
   private transient int minSize;
   private int triggerSize;
   private ArrayList pooledObjects;
   private boolean shuttingDown = false;

   public SweeperPool(int maxSize, int minSize, int intialCapacity, int sweepInterval, int triggerSize) {
      this.maxSize = this.saneConvert(maxSize);
      this.minSize = this.saneConvert(minSize);
      this.triggerSize = this.saneConvert(triggerSize);
      this.pooledObjects = new ArrayList(intialCapacity);
      if (sweepInterval > 0) {
         this.sweeper = new Sweeper(this, sweepInterval);
         this.sweeper.start();
      }

   }

   private int saneConvert(int value) {
      return value < 0 ? 0 : value;
   }

   public synchronized Object get() {
      if (this.pooledObjects.size() != 0 && !this.shuttingDown) {
         Object obj = this.pooledObjects.remove(0);
         this.objectRetrieved(obj);
         return obj;
      } else {
         return null;
      }
   }

   public synchronized boolean put(Object obj) {
      this.objectAdded(obj);
      if (obj != null && this.pooledObjects.size() < this.maxSize && !this.shuttingDown) {
         this.pooledObjects.add(obj);
         return true;
      } else {
         if (obj != null) {
            this.objectDisposed(obj);
         }

         return false;
      }
   }

   public synchronized int getSize() {
      return this.pooledObjects.size();
   }

   public void dispose() {
      this.shuttingDown = true;
      if (this.sweeper != null) {
         this.sweeper.stop();

         try {
            this.sweeper.join();
         } catch (InterruptedException var5) {
            System.err.println("Unexpected execption occurred: ");
            var5.printStackTrace();
         }
      }

      synchronized(this) {
         Object[] objects = this.pooledObjects.toArray();

         for(int i = 0; i < objects.length; ++i) {
            this.objectDisposed(objects[i]);
         }

         this.pooledObjects.clear();
      }
   }

   boolean isDisposed() {
      if (!this.shuttingDown) {
         return false;
      } else {
         return this.sweeper == null ? true : this.sweeper.hasStopped();
      }
   }

   public synchronized void trim() {
      if (this.triggerSize > 0 && this.pooledObjects.size() >= this.triggerSize || this.maxSize > 0 && this.pooledObjects.size() >= this.maxSize) {
         while(this.pooledObjects.size() > this.minSize) {
            this.objectDisposed(this.pooledObjects.remove(0));
         }
      }

   }

   public void objectDisposed(Object obj) {
   }

   public void objectAdded(Object obj) {
   }

   public void objectRetrieved(Object obj) {
   }

   private static class Sweeper implements Runnable {
      private final transient SweeperPool pool;
      private transient boolean service = false;
      private final transient int sweepInterval;
      private transient Thread t = null;

      public Sweeper(SweeperPool pool, int sweepInterval) {
         this.sweepInterval = sweepInterval;
         this.pool = pool;
      }

      public void run() {
         this.debug("started");
         if (this.sweepInterval > 0) {
            synchronized(this) {
               for(; this.service; this.runSweep()) {
                  try {
                     this.wait((long)(this.sweepInterval * 1000));
                  } catch (InterruptedException var4) {
                  }
               }
            }
         }

         this.debug("stopped");
      }

      public void start() {
         if (!this.service) {
            this.service = true;
            this.t = new Thread(this);
            this.t.setName("Sweeper");
            this.t.start();
         }

      }

      public synchronized void stop() {
         this.service = false;
         this.notifyAll();
      }

      void join() throws InterruptedException {
         this.t.join();
      }

      boolean hasStopped() {
         return !this.service && !this.t.isAlive();
      }

      private final void debug(String msg) {
      }

      private void runSweep() {
         this.debug("runningSweep. time=" + System.currentTimeMillis());
         this.pool.trim();
      }
   }
}
