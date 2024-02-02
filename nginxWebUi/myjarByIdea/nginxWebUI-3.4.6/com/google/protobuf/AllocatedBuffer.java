package com.google.protobuf;

import java.nio.ByteBuffer;

abstract class AllocatedBuffer {
   public abstract boolean hasNioBuffer();

   public abstract boolean hasArray();

   public abstract ByteBuffer nioBuffer();

   public abstract byte[] array();

   public abstract int arrayOffset();

   public abstract int position();

   public abstract AllocatedBuffer position(int var1);

   public abstract int limit();

   public abstract int remaining();

   public static AllocatedBuffer wrap(byte[] bytes) {
      return wrapNoCheck(bytes, 0, bytes.length);
   }

   public static AllocatedBuffer wrap(byte[] bytes, int offset, int length) {
      if (offset >= 0 && length >= 0 && offset + length <= bytes.length) {
         return wrapNoCheck(bytes, offset, length);
      } else {
         throw new IndexOutOfBoundsException(String.format("bytes.length=%d, offset=%d, length=%d", bytes.length, offset, length));
      }
   }

   public static AllocatedBuffer wrap(final ByteBuffer buffer) {
      Internal.checkNotNull(buffer, "buffer");
      return new AllocatedBuffer() {
         public boolean hasNioBuffer() {
            return true;
         }

         public ByteBuffer nioBuffer() {
            return buffer;
         }

         public boolean hasArray() {
            return buffer.hasArray();
         }

         public byte[] array() {
            return buffer.array();
         }

         public int arrayOffset() {
            return buffer.arrayOffset();
         }

         public int position() {
            return buffer.position();
         }

         public AllocatedBuffer position(int position) {
            buffer.position(position);
            return this;
         }

         public int limit() {
            return buffer.limit();
         }

         public int remaining() {
            return buffer.remaining();
         }
      };
   }

   private static AllocatedBuffer wrapNoCheck(final byte[] bytes, final int offset, final int length) {
      return new AllocatedBuffer() {
         private int position;

         public boolean hasNioBuffer() {
            return false;
         }

         public ByteBuffer nioBuffer() {
            throw new UnsupportedOperationException();
         }

         public boolean hasArray() {
            return true;
         }

         public byte[] array() {
            return bytes;
         }

         public int arrayOffset() {
            return offset;
         }

         public int position() {
            return this.position;
         }

         public AllocatedBuffer position(int position) {
            if (position >= 0 && position <= length) {
               this.position = position;
               return this;
            } else {
               throw new IllegalArgumentException("Invalid position: " + position);
            }
         }

         public int limit() {
            return length;
         }

         public int remaining() {
            return length - this.position;
         }
      };
   }
}
