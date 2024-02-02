package org.wildfly.common.lock;

import java.util.concurrent.locks.ReentrantLock;

class ExtendedReentrantLock extends ReentrantLock implements ExtendedLock {
   ExtendedReentrantLock(boolean fair) {
      super(fair);
   }

   ExtendedReentrantLock() {
   }
}
