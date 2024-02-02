package cn.hutool.core.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class NoReadWriteLock implements ReadWriteLock {
   public Lock readLock() {
      return NoLock.INSTANCE;
   }

   public Lock writeLock() {
      return NoLock.INSTANCE;
   }
}
