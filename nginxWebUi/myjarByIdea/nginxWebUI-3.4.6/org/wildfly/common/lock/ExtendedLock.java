package org.wildfly.common.lock;

import java.util.concurrent.locks.Lock;

public interface ExtendedLock extends Lock {
   boolean isLocked();

   boolean isHeldByCurrentThread();

   boolean isFair();
}
