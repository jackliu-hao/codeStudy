package ch.qos.logback.core.net;

import java.util.concurrent.LinkedBlockingDeque;

public class QueueFactory {
   public <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
      int actualCapacity = capacity < 1 ? 1 : capacity;
      return new LinkedBlockingDeque(actualCapacity);
   }
}
