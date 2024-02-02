package org.wildfly.common.lock;

import java.util.concurrent.locks.Lock;

public interface ExtendedLock extends Lock {
  boolean isLocked();
  
  boolean isHeldByCurrentThread();
  
  boolean isFair();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\ExtendedLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */