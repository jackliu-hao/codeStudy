package com.mysql.cj.log;

import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.protocol.Resultset;

public interface ProfilerEventHandler {
  void init(Log paramLog);
  
  void destroy();
  
  void consumeEvent(ProfilerEvent paramProfilerEvent);
  
  void processEvent(byte paramByte, Session paramSession, Query paramQuery, Resultset paramResultset, long paramLong, Throwable paramThrowable, String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\ProfilerEventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */