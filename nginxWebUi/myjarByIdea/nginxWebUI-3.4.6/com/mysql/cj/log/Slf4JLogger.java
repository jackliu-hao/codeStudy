package com.mysql.cj.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLogger implements Log {
   private Logger log;

   public Slf4JLogger(String name) {
      this.log = LoggerFactory.getLogger(name);
   }

   public boolean isDebugEnabled() {
      return this.log.isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.log.isErrorEnabled();
   }

   public boolean isFatalEnabled() {
      return this.log.isErrorEnabled();
   }

   public boolean isInfoEnabled() {
      return this.log.isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return this.log.isTraceEnabled();
   }

   public boolean isWarnEnabled() {
      return this.log.isWarnEnabled();
   }

   public void logDebug(Object msg) {
      this.log.debug(msg.toString());
   }

   public void logDebug(Object msg, Throwable thrown) {
      this.log.debug(msg.toString(), thrown);
   }

   public void logError(Object msg) {
      this.log.error(msg.toString());
   }

   public void logError(Object msg, Throwable thrown) {
      this.log.error(msg.toString(), thrown);
   }

   public void logFatal(Object msg) {
      this.log.error(msg.toString());
   }

   public void logFatal(Object msg, Throwable thrown) {
      this.log.error(msg.toString(), thrown);
   }

   public void logInfo(Object msg) {
      this.log.info(msg.toString());
   }

   public void logInfo(Object msg, Throwable thrown) {
      this.log.info(msg.toString(), thrown);
   }

   public void logTrace(Object msg) {
      this.log.trace(msg.toString());
   }

   public void logTrace(Object msg, Throwable thrown) {
      this.log.trace(msg.toString(), thrown);
   }

   public void logWarn(Object msg) {
      this.log.warn(msg.toString());
   }

   public void logWarn(Object msg, Throwable thrown) {
      this.log.warn(msg.toString(), thrown);
   }
}
