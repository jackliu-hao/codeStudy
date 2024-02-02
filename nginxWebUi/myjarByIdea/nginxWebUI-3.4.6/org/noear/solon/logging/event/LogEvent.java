package org.noear.solon.logging.event;

import java.util.Map;

public class LogEvent {
   private String loggerName;
   private Level level;
   private Map<String, String> metainfo;
   private Object content;
   private long timeStamp;
   private String threadName;
   private Throwable throwable;

   public LogEvent(String loggerName, Level level, Map<String, String> metainfo, Object content, long timeStamp, String threadName, Throwable throwable) {
      this.loggerName = loggerName;
      this.level = level;
      this.metainfo = metainfo;
      this.content = content;
      this.timeStamp = timeStamp;
      this.threadName = threadName;
      this.throwable = throwable;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public Level getLevel() {
      return this.level;
   }

   public Map<String, String> getMetainfo() {
      return this.metainfo;
   }

   public Object getContent() {
      return this.content;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }
}
