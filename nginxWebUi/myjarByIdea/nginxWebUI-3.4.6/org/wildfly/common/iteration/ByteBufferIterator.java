package org.wildfly.common.iteration;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

final class ByteBufferIterator extends ByteIterator {
   private final ByteBuffer buffer;
   private final int initialPosition;

   ByteBufferIterator(ByteBuffer buffer) {
      this.buffer = buffer;
      this.initialPosition = buffer.position();
   }

   public boolean hasNext() {
      return this.buffer.hasRemaining();
   }

   public boolean hasPrevious() {
      return this.buffer.position() > this.initialPosition;
   }

   public int next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.buffer.get() & 255;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.buffer.get(this.buffer.position()) & 255;
      }
   }

   public int previous() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         this.buffer.position(this.buffer.position() - 1);
         return this.peekNext();
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.buffer.get(this.buffer.position() - 1) & 255;
      }
   }

   public long getIndex() {
      return (long)(this.buffer.position() - this.initialPosition);
   }
}
