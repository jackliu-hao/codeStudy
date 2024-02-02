package org.h2.mvstore;

import java.nio.ByteBuffer;

public class WriteBuffer {
   private static final int MAX_REUSE_CAPACITY = 4194304;
   private static final int MIN_GROW = 1048576;
   private ByteBuffer reuse;
   private ByteBuffer buff;

   public WriteBuffer(int var1) {
      this.reuse = ByteBuffer.allocate(var1);
      this.buff = this.reuse;
   }

   public WriteBuffer() {
      this(1048576);
   }

   public WriteBuffer putVarInt(int var1) {
      DataUtils.writeVarInt(this.ensureCapacity(5), var1);
      return this;
   }

   public WriteBuffer putVarLong(long var1) {
      DataUtils.writeVarLong(this.ensureCapacity(10), var1);
      return this;
   }

   public WriteBuffer putStringData(String var1, int var2) {
      ByteBuffer var3 = this.ensureCapacity(3 * var2);
      DataUtils.writeStringData(var3, var1, var2);
      return this;
   }

   public WriteBuffer put(byte var1) {
      this.ensureCapacity(1).put(var1);
      return this;
   }

   public WriteBuffer putChar(char var1) {
      this.ensureCapacity(2).putChar(var1);
      return this;
   }

   public WriteBuffer putShort(short var1) {
      this.ensureCapacity(2).putShort(var1);
      return this;
   }

   public WriteBuffer putInt(int var1) {
      this.ensureCapacity(4).putInt(var1);
      return this;
   }

   public WriteBuffer putLong(long var1) {
      this.ensureCapacity(8).putLong(var1);
      return this;
   }

   public WriteBuffer putFloat(float var1) {
      this.ensureCapacity(4).putFloat(var1);
      return this;
   }

   public WriteBuffer putDouble(double var1) {
      this.ensureCapacity(8).putDouble(var1);
      return this;
   }

   public WriteBuffer put(byte[] var1) {
      this.ensureCapacity(var1.length).put(var1);
      return this;
   }

   public WriteBuffer put(byte[] var1, int var2, int var3) {
      this.ensureCapacity(var3).put(var1, var2, var3);
      return this;
   }

   public WriteBuffer put(ByteBuffer var1) {
      this.ensureCapacity(var1.remaining()).put(var1);
      return this;
   }

   public WriteBuffer limit(int var1) {
      this.ensureCapacity(var1 - this.buff.position()).limit(var1);
      return this;
   }

   public int capacity() {
      return this.buff.capacity();
   }

   public WriteBuffer position(int var1) {
      this.buff.position(var1);
      return this;
   }

   public int limit() {
      return this.buff.limit();
   }

   public int position() {
      return this.buff.position();
   }

   public WriteBuffer get(byte[] var1) {
      this.buff.get(var1);
      return this;
   }

   public WriteBuffer putInt(int var1, int var2) {
      this.buff.putInt(var1, var2);
      return this;
   }

   public WriteBuffer putShort(int var1, short var2) {
      this.buff.putShort(var1, var2);
      return this;
   }

   public WriteBuffer clear() {
      if (this.buff.limit() > 4194304) {
         this.buff = this.reuse;
      } else if (this.buff != this.reuse) {
         this.reuse = this.buff;
      }

      this.buff.clear();
      return this;
   }

   public ByteBuffer getBuffer() {
      return this.buff;
   }

   private ByteBuffer ensureCapacity(int var1) {
      if (this.buff.remaining() < var1) {
         this.grow(var1);
      }

      return this.buff;
   }

   private void grow(int var1) {
      ByteBuffer var2 = this.buff;
      int var3 = var1 - var2.remaining();
      long var4 = (long)Math.max(var3, 1048576);
      var4 = Math.max((long)(var2.capacity() / 2), var4);
      int var6 = (int)Math.min(2147483647L, (long)var2.capacity() + var4);
      if (var6 < var3) {
         throw new OutOfMemoryError("Capacity: " + var6 + " needed: " + var3);
      } else {
         try {
            this.buff = ByteBuffer.allocate(var6);
         } catch (OutOfMemoryError var8) {
            throw new OutOfMemoryError("Capacity: " + var6);
         }

         var2.flip();
         this.buff.put(var2);
         if (var6 <= 4194304) {
            this.reuse = this.buff;
         }

      }
   }
}
