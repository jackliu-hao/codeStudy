package org.h2.mvstore;

public final class RootReference<K, V> {
   public final Page<K, V> root;
   public final long version;
   private final byte holdCount;
   private final long ownerId;
   volatile RootReference<K, V> previous;
   final long updateCounter;
   final long updateAttemptCounter;
   private final byte appendCounter;

   RootReference(Page<K, V> var1, long var2) {
      this.root = var1;
      this.version = var2;
      this.previous = null;
      this.updateCounter = 1L;
      this.updateAttemptCounter = 1L;
      this.holdCount = 0;
      this.ownerId = 0L;
      this.appendCounter = 0;
   }

   private RootReference(RootReference<K, V> var1, Page<K, V> var2, long var3) {
      this.root = var2;
      this.version = var1.version;
      this.previous = var1.previous;
      this.updateCounter = var1.updateCounter + 1L;
      this.updateAttemptCounter = var1.updateAttemptCounter + var3;
      this.holdCount = 0;
      this.ownerId = 0L;
      this.appendCounter = var1.appendCounter;
   }

   private RootReference(RootReference<K, V> var1, int var2) {
      this.root = var1.root;
      this.version = var1.version;
      this.previous = var1.previous;
      this.updateCounter = var1.updateCounter + 1L;
      this.updateAttemptCounter = var1.updateAttemptCounter + (long)var2;

      assert var1.holdCount == 0 || var1.ownerId == Thread.currentThread().getId() : Thread.currentThread().getId() + " " + var1;

      this.holdCount = (byte)(var1.holdCount + 1);
      this.ownerId = Thread.currentThread().getId();
      this.appendCounter = var1.appendCounter;
   }

   private RootReference(RootReference<K, V> var1, Page<K, V> var2, boolean var3, int var4) {
      this.root = var2;
      this.version = var1.version;
      this.previous = var1.previous;
      this.updateCounter = var1.updateCounter;
      this.updateAttemptCounter = var1.updateAttemptCounter;

      assert var1.holdCount > 0 && var1.ownerId == Thread.currentThread().getId() : Thread.currentThread().getId() + " " + var1;

      this.holdCount = (byte)(var1.holdCount - (var3 ? 0 : 1));
      this.ownerId = this.holdCount == 0 ? 0L : Thread.currentThread().getId();
      this.appendCounter = (byte)var4;
   }

   private RootReference(RootReference<K, V> var1, long var2, int var4) {
      RootReference var5;
      RootReference var6;
      for(var5 = var1; (var6 = var5.previous) != null && var6.root == var1.root; var5 = var6) {
      }

      this.root = var1.root;
      this.version = var2;
      this.previous = var5;
      this.updateCounter = var1.updateCounter + 1L;
      this.updateAttemptCounter = var1.updateAttemptCounter + (long)var4;
      this.holdCount = var1.holdCount == 0 ? 0 : (byte)(var1.holdCount - 1);
      this.ownerId = this.holdCount == 0 ? 0L : var1.ownerId;

      assert var1.appendCounter == 0;

      this.appendCounter = 0;
   }

   RootReference<K, V> updateRootPage(Page<K, V> var1, long var2) {
      return this.isFree() ? this.tryUpdate(new RootReference(this, var1, var2)) : null;
   }

   RootReference<K, V> tryLock(int var1) {
      return this.canUpdate() ? this.tryUpdate(new RootReference(this, var1)) : null;
   }

   RootReference<K, V> tryUnlockAndUpdateVersion(long var1, int var3) {
      return this.canUpdate() ? this.tryUpdate(new RootReference(this, var1, var3)) : null;
   }

   RootReference<K, V> updatePageAndLockedStatus(Page<K, V> var1, boolean var2, int var3) {
      return this.canUpdate() ? this.tryUpdate(new RootReference(this, var1, var2, var3)) : null;
   }

   void removeUnusedOldVersions(long var1) {
      for(RootReference var3 = this; var3 != null; var3 = var3.previous) {
         if (var3.version < var1) {
            RootReference var4;

            assert (var4 = var3.previous) == null || var4.getAppendCounter() == 0 : var1 + " " + var3.previous;

            var3.previous = null;
         }
      }

   }

   boolean isLocked() {
      return this.holdCount != 0;
   }

   private boolean isFree() {
      return this.holdCount == 0;
   }

   private boolean canUpdate() {
      return this.isFree() || this.ownerId == Thread.currentThread().getId();
   }

   public boolean isLockedByCurrentThread() {
      return this.holdCount != 0 && this.ownerId == Thread.currentThread().getId();
   }

   private RootReference<K, V> tryUpdate(RootReference<K, V> var1) {
      assert this.canUpdate();

      return this.root.map.compareAndSetRoot(this, var1) ? var1 : null;
   }

   long getVersion() {
      RootReference var1 = this.previous;
      return var1 != null && var1.root == this.root && var1.appendCounter == this.appendCounter ? var1.getVersion() : this.version;
   }

   boolean hasChangesSince(long var1, boolean var3) {
      boolean var10000;
      label20: {
         if (var3) {
            if (this.root.isSaved()) {
               if (this.getAppendCounter() > 0) {
                  break label20;
               }
            } else if (this.getTotalCount() > 0L) {
               break label20;
            }
         }

         if (this.getVersion() <= var1) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   int getAppendCounter() {
      return this.appendCounter & 255;
   }

   public boolean needFlush() {
      return this.appendCounter != 0;
   }

   public long getTotalCount() {
      return this.root.getTotalCount() + (long)this.getAppendCounter();
   }

   public String toString() {
      return "RootReference(" + System.identityHashCode(this.root) + ", v=" + this.version + ", owner=" + this.ownerId + (this.ownerId == Thread.currentThread().getId() ? "(current)" : "") + ", holdCnt=" + this.holdCount + ", keys=" + this.root.getTotalCount() + ", append=" + this.getAppendCounter() + ")";
   }
}
