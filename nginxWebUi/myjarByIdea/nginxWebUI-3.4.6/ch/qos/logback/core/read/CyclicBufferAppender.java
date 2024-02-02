package ch.qos.logback.core.read;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.helpers.CyclicBuffer;

public class CyclicBufferAppender<E> extends AppenderBase<E> {
   CyclicBuffer<E> cb;
   int maxSize = 512;

   public void start() {
      this.cb = new CyclicBuffer(this.maxSize);
      super.start();
   }

   public void stop() {
      this.cb = null;
      super.stop();
   }

   protected void append(E eventObject) {
      if (this.isStarted()) {
         this.cb.add(eventObject);
      }
   }

   public int getLength() {
      return this.isStarted() ? this.cb.length() : 0;
   }

   public E get(int i) {
      return this.isStarted() ? this.cb.get(i) : null;
   }

   public void reset() {
      this.cb.clear();
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
   }
}
