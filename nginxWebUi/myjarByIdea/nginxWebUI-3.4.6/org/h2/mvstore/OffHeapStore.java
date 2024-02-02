package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class OffHeapStore extends FileStore {
   private final TreeMap<Long, ByteBuffer> memory = new TreeMap();

   public void open(String var1, boolean var2, char[] var3) {
      this.memory.clear();
   }

   public String toString() {
      return this.memory.toString();
   }

   public ByteBuffer readFully(long var1, int var3) {
      Map.Entry var4 = this.memory.floorEntry(var1);
      if (var4 == null) {
         throw DataUtils.newMVStoreException(1, "Could not read from position {0}", var1);
      } else {
         this.readCount.incrementAndGet();
         this.readBytes.addAndGet((long)var3);
         ByteBuffer var5 = (ByteBuffer)var4.getValue();
         ByteBuffer var6 = var5.duplicate();
         int var7 = (int)(var1 - (Long)var4.getKey());
         var6.position(var7);
         var6.limit(var3 + var7);
         return var6.slice();
      }
   }

   public void free(long var1, int var3) {
      this.freeSpace.free(var1, var3);
      ByteBuffer var4 = (ByteBuffer)this.memory.remove(var1);
      if (var4 != null && var4.remaining() != var3) {
         throw DataUtils.newMVStoreException(1, "Partial remove is not supported at position {0}", var1);
      }
   }

   public void writeFully(long var1, ByteBuffer var3) {
      this.fileSize = Math.max(this.fileSize, var1 + (long)var3.remaining());
      Map.Entry var4 = this.memory.floorEntry(var1);
      if (var4 == null) {
         this.writeNewEntry(var1, var3);
      } else {
         long var5 = (Long)var4.getKey();
         ByteBuffer var7 = (ByteBuffer)var4.getValue();
         int var8 = var7.capacity();
         int var9 = var3.remaining();
         if (var5 == var1) {
            if (var8 != var9) {
               throw DataUtils.newMVStoreException(1, "Could not write to position {0}; partial overwrite is not supported", var1);
            } else {
               this.writeCount.incrementAndGet();
               this.writeBytes.addAndGet((long)var9);
               var7.rewind();
               var7.put(var3);
            }
         } else if (var5 + (long)var8 > var1) {
            throw DataUtils.newMVStoreException(1, "Could not write to position {0}; partial overwrite is not supported", var1);
         } else {
            this.writeNewEntry(var1, var3);
         }
      }
   }

   private void writeNewEntry(long var1, ByteBuffer var3) {
      int var4 = var3.remaining();
      this.writeCount.incrementAndGet();
      this.writeBytes.addAndGet((long)var4);
      ByteBuffer var5 = ByteBuffer.allocateDirect(var4);
      var5.put(var3);
      var5.rewind();
      this.memory.put(var1, var5);
   }

   public void truncate(long var1) {
      this.writeCount.incrementAndGet();
      if (var1 == 0L) {
         this.fileSize = 0L;
         this.memory.clear();
      } else {
         this.fileSize = var1;
         Iterator var3 = this.memory.keySet().iterator();

         while(var3.hasNext()) {
            long var4 = (Long)var3.next();
            if (var4 < var1) {
               break;
            }

            ByteBuffer var6 = (ByteBuffer)this.memory.get(var4);
            if ((long)var6.capacity() > var1) {
               throw DataUtils.newMVStoreException(1, "Could not truncate to {0}; partial truncate is not supported", var4);
            }

            var3.remove();
         }

      }
   }

   public void close() {
      this.memory.clear();
   }

   public void sync() {
   }

   public int getDefaultRetentionTime() {
      return 0;
   }
}
