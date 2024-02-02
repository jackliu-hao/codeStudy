package ch.qos.logback.core.helpers;

import java.util.ArrayList;
import java.util.List;

public class CyclicBuffer<E> {
   E[] ea;
   int first;
   int last;
   int numElems;
   int maxSize;

   public CyclicBuffer(int maxSize) throws IllegalArgumentException {
      if (maxSize < 1) {
         throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
      } else {
         this.init(maxSize);
      }
   }

   public CyclicBuffer(CyclicBuffer<E> other) {
      this.maxSize = other.maxSize;
      this.ea = (Object[])(new Object[this.maxSize]);
      System.arraycopy(other.ea, 0, this.ea, 0, this.maxSize);
      this.last = other.last;
      this.first = other.first;
      this.numElems = other.numElems;
   }

   private void init(int maxSize) {
      this.maxSize = maxSize;
      this.ea = (Object[])(new Object[maxSize]);
      this.first = 0;
      this.last = 0;
      this.numElems = 0;
   }

   public void clear() {
      this.init(this.maxSize);
   }

   public void add(E event) {
      this.ea[this.last] = event;
      if (++this.last == this.maxSize) {
         this.last = 0;
      }

      if (this.numElems < this.maxSize) {
         ++this.numElems;
      } else if (++this.first == this.maxSize) {
         this.first = 0;
      }

   }

   public E get(int i) {
      return i >= 0 && i < this.numElems ? this.ea[(this.first + i) % this.maxSize] : null;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public E get() {
      E r = null;
      if (this.numElems > 0) {
         --this.numElems;
         r = this.ea[this.first];
         this.ea[this.first] = null;
         if (++this.first == this.maxSize) {
            this.first = 0;
         }
      }

      return r;
   }

   public List<E> asList() {
      List<E> tList = new ArrayList();

      for(int i = 0; i < this.length(); ++i) {
         tList.add(this.get(i));
      }

      return tList;
   }

   public int length() {
      return this.numElems;
   }

   public void resize(int newSize) {
      if (newSize < 0) {
         throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
      } else if (newSize != this.numElems) {
         E[] temp = (Object[])(new Object[newSize]);
         int loopLen = newSize < this.numElems ? newSize : this.numElems;

         for(int i = 0; i < loopLen; ++i) {
            temp[i] = this.ea[this.first];
            this.ea[this.first] = null;
            if (++this.first == this.numElems) {
               this.first = 0;
            }
         }

         this.ea = temp;
         this.first = 0;
         this.numElems = loopLen;
         this.maxSize = newSize;
         if (loopLen == newSize) {
            this.last = 0;
         } else {
            this.last = loopLen;
         }

      }
   }
}
