package org.h2.mvstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class StreamStore {
   private final Map<Long, byte[]> map;
   private int minBlockSize = 256;
   private int maxBlockSize = 262144;
   private final AtomicLong nextKey = new AtomicLong();
   private final AtomicReference<byte[]> nextBuffer = new AtomicReference();

   public StreamStore(Map<Long, byte[]> var1) {
      this.map = var1;
   }

   public Map<Long, byte[]> getMap() {
      return this.map;
   }

   public void setNextKey(long var1) {
      this.nextKey.set(var1);
   }

   public long getNextKey() {
      return this.nextKey.get();
   }

   public void setMinBlockSize(int var1) {
      this.minBlockSize = var1;
   }

   public int getMinBlockSize() {
      return this.minBlockSize;
   }

   public void setMaxBlockSize(int var1) {
      this.maxBlockSize = var1;
   }

   public long getMaxBlockSize() {
      return (long)this.maxBlockSize;
   }

   public byte[] put(InputStream var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      int var3 = 0;

      try {
         while(!this.put(var2, var1, var3)) {
            if (var2.size() > this.maxBlockSize / 2) {
               var2 = this.putIndirectId(var2);
               ++var3;
            }
         }
      } catch (IOException var5) {
         this.remove(var2.toByteArray());
         throw var5;
      }

      if (var2.size() > this.minBlockSize * 2) {
         var2 = this.putIndirectId(var2);
      }

      return var2.toByteArray();
   }

   private boolean put(ByteArrayOutputStream var1, InputStream var2, int var3) throws IOException {
      if (var3 <= 0) {
         byte[] var8 = (byte[])this.nextBuffer.getAndSet((Object)null);
         if (var8 == null) {
            var8 = new byte[this.maxBlockSize];
         }

         byte[] var9 = read(var2, var8);
         if (var9 != var8) {
            this.nextBuffer.set(var8);
         }

         int var6 = var9.length;
         if (var6 == 0) {
            return true;
         } else {
            boolean var7 = var6 < this.maxBlockSize;
            if (var6 < this.minBlockSize) {
               var1.write(0);
               DataUtils.writeVarInt((OutputStream)var1, var6);
               var1.write(var9);
            } else {
               var1.write(1);
               DataUtils.writeVarInt((OutputStream)var1, var6);
               DataUtils.writeVarLong((OutputStream)var1, this.writeBlock(var9));
            }

            return var7;
         }
      } else {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();

         boolean var5;
         do {
            var5 = this.put(var4, var2, var3 - 1);
            if (var4.size() > this.maxBlockSize / 2) {
               var4 = this.putIndirectId(var4);
               var4.writeTo(var1);
               return var5;
            }
         } while(!var5);

         var4.writeTo(var1);
         return true;
      }
   }

   private static byte[] read(InputStream var0, byte[] var1) throws IOException {
      int var2 = 0;
      int var3 = var1.length;

      while(var3 > 0) {
         try {
            int var4 = var0.read(var1, var2, var3);
            if (var4 < 0) {
               return Arrays.copyOf(var1, var2);
            }

            var2 += var4;
            var3 -= var4;
         } catch (RuntimeException var5) {
            throw new IOException(var5);
         }
      }

      return var1;
   }

   private ByteArrayOutputStream putIndirectId(ByteArrayOutputStream var1) throws IOException {
      byte[] var2 = var1.toByteArray();
      var1 = new ByteArrayOutputStream();
      var1.write(2);
      DataUtils.writeVarLong((OutputStream)var1, this.length(var2));
      DataUtils.writeVarLong((OutputStream)var1, this.writeBlock(var2));
      return var1;
   }

   private long writeBlock(byte[] var1) {
      long var2 = this.getAndIncrementNextKey();
      this.map.put(var2, var1);
      this.onStore(var1.length);
      return var2;
   }

   protected void onStore(int var1) {
   }

   private long getAndIncrementNextKey() {
      long var1 = this.nextKey.getAndIncrement();
      if (!this.map.containsKey(var1)) {
         return var1;
      } else {
         synchronized(this) {
            long var4 = var1;
            long var6 = Long.MAX_VALUE;

            while(var4 < var6) {
               long var8 = var4 + var6 >>> 1;
               if (this.map.containsKey(var8)) {
                  var4 = var8 + 1L;
               } else {
                  var6 = var8;
               }
            }

            this.nextKey.set(var4 + 1L);
            return var4;
         }
      }
   }

   public long getMaxBlockKey(byte[] var1) {
      long var2 = -1L;
      ByteBuffer var4 = ByteBuffer.wrap(var1);

      while(var4.hasRemaining()) {
         switch (var4.get()) {
            case 0:
               int var5 = DataUtils.readVarInt(var4);
               var4.position(var4.position() + var5);
               break;
            case 1:
               DataUtils.readVarInt(var4);
               long var6 = DataUtils.readVarLong(var4);
               var2 = Math.max(var2, var6);
               break;
            case 2:
               DataUtils.readVarLong(var4);
               long var8 = DataUtils.readVarLong(var4);
               var2 = var8;
               byte[] var10 = (byte[])this.map.get(var8);
               long var11 = this.getMaxBlockKey(var10);
               if (var11 >= 0L) {
                  var2 = Math.max(var8, var11);
               }
               break;
            default:
               throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(var1));
         }
      }

      return var2;
   }

   public void remove(byte[] var1) {
      ByteBuffer var2 = ByteBuffer.wrap(var1);

      while(var2.hasRemaining()) {
         switch (var2.get()) {
            case 0:
               int var3 = DataUtils.readVarInt(var2);
               var2.position(var2.position() + var3);
               break;
            case 1:
               DataUtils.readVarInt(var2);
               long var4 = DataUtils.readVarLong(var2);
               this.map.remove(var4);
               break;
            case 2:
               DataUtils.readVarLong(var2);
               long var6 = DataUtils.readVarLong(var2);
               this.remove((byte[])this.map.get(var6));
               this.map.remove(var6);
               break;
            default:
               throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(var1));
         }
      }

   }

   public static String toString(byte[] var0) {
      StringBuilder var1 = new StringBuilder();
      ByteBuffer var2 = ByteBuffer.wrap(var0);

      long var3;
      for(var3 = 0L; var2.hasRemaining(); var1.append(", ")) {
         long var5;
         int var7;
         switch (var2.get()) {
            case 0:
               var7 = DataUtils.readVarInt(var2);
               var2.position(var2.position() + var7);
               var1.append("data len=").append(var7);
               var3 += (long)var7;
               break;
            case 1:
               var7 = DataUtils.readVarInt(var2);
               var3 += (long)var7;
               var5 = DataUtils.readVarLong(var2);
               var1.append("block ").append(var5).append(" len=").append(var7);
               break;
            case 2:
               var7 = DataUtils.readVarInt(var2);
               var3 += DataUtils.readVarLong(var2);
               var5 = DataUtils.readVarLong(var2);
               var1.append("indirect block ").append(var5).append(" len=").append(var7);
               break;
            default:
               var1.append("error");
         }
      }

      var1.append("length=").append(var3);
      return var1.toString();
   }

   public long length(byte[] var1) {
      ByteBuffer var2 = ByteBuffer.wrap(var1);
      long var3 = 0L;

      while(var2.hasRemaining()) {
         switch (var2.get()) {
            case 0:
               int var5 = DataUtils.readVarInt(var2);
               var2.position(var2.position() + var5);
               var3 += (long)var5;
               break;
            case 1:
               var3 += (long)DataUtils.readVarInt(var2);
               DataUtils.readVarLong(var2);
               break;
            case 2:
               var3 += DataUtils.readVarLong(var2);
               DataUtils.readVarLong(var2);
               break;
            default:
               throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(var1));
         }
      }

      return var3;
   }

   public boolean isInPlace(byte[] var1) {
      ByteBuffer var2 = ByteBuffer.wrap(var1);

      while(var2.hasRemaining()) {
         if (var2.get() != 0) {
            return false;
         }

         int var3 = DataUtils.readVarInt(var2);
         var2.position(var2.position() + var3);
      }

      return true;
   }

   public InputStream get(byte[] var1) {
      return new Stream(this, var1);
   }

   byte[] getBlock(long var1) {
      byte[] var3 = (byte[])this.map.get(var1);
      if (var3 == null) {
         throw DataUtils.newMVStoreException(50, "Block {0} not found", var1);
      } else {
         return var3;
      }
   }

   static class Stream extends InputStream {
      private final StreamStore store;
      private byte[] oneByteBuffer;
      private ByteBuffer idBuffer;
      private ByteArrayInputStream buffer;
      private long skip;
      private final long length;
      private long pos;

      Stream(StreamStore var1, byte[] var2) {
         this.store = var1;
         this.length = var1.length(var2);
         this.idBuffer = ByteBuffer.wrap(var2);
      }

      public int read() throws IOException {
         byte[] var1 = this.oneByteBuffer;
         if (var1 == null) {
            var1 = this.oneByteBuffer = new byte[1];
         }

         int var2 = this.read(var1, 0, 1);
         return var2 == -1 ? -1 : var1[0] & 255;
      }

      public long skip(long var1) {
         var1 = Math.min(this.length - this.pos, var1);
         if (var1 == 0L) {
            return 0L;
         } else {
            if (this.buffer != null) {
               long var3 = this.buffer.skip(var1);
               if (var3 > 0L) {
                  var1 = var3;
               } else {
                  this.buffer = null;
                  this.skip += var1;
               }
            } else {
               this.skip += var1;
            }

            this.pos += var1;
            return var1;
         }
      }

      public void close() {
         this.buffer = null;
         this.idBuffer.position(this.idBuffer.limit());
         this.pos = this.length;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 <= 0) {
            return 0;
         } else {
            while(true) {
               if (this.buffer == null) {
                  try {
                     this.buffer = this.nextBuffer();
                  } catch (MVStoreException var6) {
                     String var5 = DataUtils.formatMessage(50, "Block not found in id {0}", Arrays.toString(this.idBuffer.array()));
                     throw new IOException(var5, var6);
                  }

                  if (this.buffer == null) {
                     return -1;
                  }
               }

               int var4 = this.buffer.read(var1, var2, var3);
               if (var4 > 0) {
                  this.pos += (long)var4;
                  return var4;
               }

               this.buffer = null;
            }
         }
      }

      private ByteArrayInputStream nextBuffer() {
         while(true) {
            if (this.idBuffer.hasRemaining()) {
               int var7;
               switch (this.idBuffer.get()) {
                  case 0:
                     var7 = DataUtils.readVarInt(this.idBuffer);
                     if (this.skip >= (long)var7) {
                        this.skip -= (long)var7;
                        this.idBuffer.position(this.idBuffer.position() + var7);
                        continue;
                     }

                     int var8 = (int)((long)this.idBuffer.position() + this.skip);
                     int var9 = (int)((long)var7 - this.skip);
                     this.idBuffer.position(var8 + var9);
                     return new ByteArrayInputStream(this.idBuffer.array(), var8, var9);
                  case 1:
                     var7 = DataUtils.readVarInt(this.idBuffer);
                     long var2 = DataUtils.readVarLong(this.idBuffer);
                     if (this.skip >= (long)var7) {
                        this.skip -= (long)var7;
                        continue;
                     }

                     byte[] var4 = this.store.getBlock(var2);
                     int var10 = (int)this.skip;
                     this.skip = 0L;
                     return new ByteArrayInputStream(var4, var10, var4.length - var10);
                  case 2:
                     long var1 = DataUtils.readVarLong(this.idBuffer);
                     long var3 = DataUtils.readVarLong(this.idBuffer);
                     if (this.skip >= var1) {
                        this.skip -= var1;
                        continue;
                     }

                     byte[] var5 = this.store.getBlock(var3);
                     ByteBuffer var6 = ByteBuffer.allocate(var5.length + this.idBuffer.limit() - this.idBuffer.position());
                     var6.put(var5);
                     var6.put(this.idBuffer);
                     var6.flip();
                     this.idBuffer = var6;
                     return this.nextBuffer();
                  default:
                     throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(this.idBuffer.array()));
               }
            }

            return null;
         }
      }
   }
}
