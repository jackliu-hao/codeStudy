package org.slf4j.event;

import org.slf4j.Marker;

public interface LoggingEvent {
  Level getLevel();
  
  Marker getMarker();
  
  String getLoggerName();
  
  String getMessage();
  
  String getThreadName();
  
  Object[] getArgumentArray();
  
  long getTimeStamp();
  
  Throwable getThrowable();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\event\LoggingEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */