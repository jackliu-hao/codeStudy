package org.h2.util;

import org.h2.message.Trace;

public interface CacheWriter {
  void writeBack(CacheObject paramCacheObject);
  
  void flushLog();
  
  Trace getTrace();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CacheWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */