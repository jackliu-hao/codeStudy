package ch.qos.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

public final class Logger implements org.slf4j.Logger, LocationAwareLogger, AppenderAttachable<ILoggingEvent>, Serializable {
   private static final long serialVersionUID = 5454405123156820674L;
   public static final String FQCN = Logger.class.getName();
   private String name;
   private transient Level level;
   private transient int effectiveLevelInt;
   private transient Logger parent;
   private transient List<Logger> childrenList;
   private transient AppenderAttachableImpl<ILoggingEvent> aai;
   private transient boolean additive = true;
   final transient LoggerContext loggerContext;

   Logger(String name, Logger parent, LoggerContext loggerContext) {
      this.name = name;
      this.parent = parent;
      this.loggerContext = loggerContext;
   }

   public Level getEffectiveLevel() {
      return Level.toLevel(this.effectiveLevelInt);
   }

   int getEffectiveLevelInt() {
      return this.effectiveLevelInt;
   }

   public Level getLevel() {
      return this.level;
   }

   public String getName() {
      return this.name;
   }

   private boolean isRootLogger() {
      return this.parent == null;
   }

   Logger getChildByName(String childName) {
      if (this.childrenList == null) {
         return null;
      } else {
         int len = this.childrenList.size();

         for(int i = 0; i < len; ++i) {
            Logger childLogger_i = (Logger)this.childrenList.get(i);
            String childName_i = childLogger_i.getName();
            if (childName.equals(childName_i)) {
               return childLogger_i;
            }
         }

         return null;
      }
   }

   public synchronized void setLevel(Level newLevel) {
      if (this.level != newLevel) {
         if (newLevel == null && this.isRootLogger()) {
            throw new IllegalArgumentException("The level of the root logger cannot be set to null");
         } else {
            this.level = newLevel;
            if (newLevel == null) {
               this.effectiveLevelInt = this.parent.effectiveLevelInt;
               newLevel = this.parent.getEffectiveLevel();
            } else {
               this.effectiveLevelInt = newLevel.levelInt;
            }

            if (this.childrenList != null) {
               int len = this.childrenList.size();

               for(int i = 0; i < len; ++i) {
                  Logger child = (Logger)this.childrenList.get(i);
                  child.handleParentLevelChange(this.effectiveLevelInt);
               }
            }

            this.loggerContext.fireOnLevelChange(this, newLevel);
         }
      }
   }

   private synchronized void handleParentLevelChange(int newParentLevelInt) {
      if (this.level == null) {
         this.effectiveLevelInt = newParentLevelInt;
         if (this.childrenList != null) {
            int len = this.childrenList.size();

            for(int i = 0; i < len; ++i) {
               Logger child = (Logger)this.childrenList.get(i);
               child.handleParentLevelChange(newParentLevelInt);
            }
         }
      }

   }

   public void detachAndStopAllAppenders() {
      if (this.aai != null) {
         this.aai.detachAndStopAllAppenders();
      }

   }

   public boolean detachAppender(String name) {
      return this.aai == null ? false : this.aai.detachAppender(name);
   }

   public synchronized void addAppender(Appender<ILoggingEvent> newAppender) {
      if (this.aai == null) {
         this.aai = new AppenderAttachableImpl();
      }

      this.aai.addAppender(newAppender);
   }

   public boolean isAttached(Appender<ILoggingEvent> appender) {
      return this.aai == null ? false : this.aai.isAttached(appender);
   }

   public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
      return this.aai == null ? Collections.EMPTY_LIST.iterator() : this.aai.iteratorForAppenders();
   }

   public Appender<ILoggingEvent> getAppender(String name) {
      return this.aai == null ? null : this.aai.getAppender(name);
   }

   public void callAppenders(ILoggingEvent event) {
      int writes = 0;

      for(Logger l = this; l != null; l = l.parent) {
         writes += l.appendLoopOnAppenders(event);
         if (!l.additive) {
            break;
         }
      }

      if (writes == 0) {
         this.loggerContext.noAppenderDefinedWarning(this);
      }

   }

   private int appendLoopOnAppenders(ILoggingEvent event) {
      return this.aai != null ? this.aai.appendLoopOnAppenders(event) : 0;
   }

   public boolean detachAppender(Appender<ILoggingEvent> appender) {
      return this.aai == null ? false : this.aai.detachAppender(appender);
   }

   Logger createChildByLastNamePart(String lastPart) {
      int i_index = LoggerNameUtil.getFirstSeparatorIndexOf(lastPart);
      if (i_index != -1) {
         throw new IllegalArgumentException("Child name [" + lastPart + " passed as parameter, may not include [" + '.' + "]");
      } else {
         if (this.childrenList == null) {
            this.childrenList = new CopyOnWriteArrayList();
         }

         Logger childLogger;
         if (this.isRootLogger()) {
            childLogger = new Logger(lastPart, this, this.loggerContext);
         } else {
            childLogger = new Logger(this.name + '.' + lastPart, this, this.loggerContext);
         }

         this.childrenList.add(childLogger);
         childLogger.effectiveLevelInt = this.effectiveLevelInt;
         return childLogger;
      }
   }

   private void localLevelReset() {
      this.effectiveLevelInt = 10000;
      if (this.isRootLogger()) {
         this.level = Level.DEBUG;
      } else {
         this.level = null;
      }

   }

   void recursiveReset() {
      this.detachAndStopAllAppenders();
      this.localLevelReset();
      this.additive = true;
      if (this.childrenList != null) {
         Iterator var1 = this.childrenList.iterator();

         while(var1.hasNext()) {
            Logger childLogger = (Logger)var1.next();
            childLogger.recursiveReset();
         }

      }
   }

   Logger createChildByName(String childName) {
      int i_index = LoggerNameUtil.getSeparatorIndexOf(childName, this.name.length() + 1);
      if (i_index != -1) {
         throw new IllegalArgumentException("For logger [" + this.name + "] child name [" + childName + " passed as parameter, may not include '.' after index" + (this.name.length() + 1));
      } else {
         if (this.childrenList == null) {
            this.childrenList = new CopyOnWriteArrayList();
         }

         Logger childLogger = new Logger(childName, this, this.loggerContext);
         this.childrenList.add(childLogger);
         childLogger.effectiveLevelInt = this.effectiveLevelInt;
         return childLogger;
      }
   }

   private void filterAndLog_0_Or3Plus(String localFQCN, Marker marker, Level level, String msg, Object[] params, Throwable t) {
      FilterReply decision = this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level, msg, params, t);
      if (decision == FilterReply.NEUTRAL) {
         if (this.effectiveLevelInt > level.levelInt) {
            return;
         }
      } else if (decision == FilterReply.DENY) {
         return;
      }

      this.buildLoggingEventAndAppend(localFQCN, marker, level, msg, params, t);
   }

   private void filterAndLog_1(String localFQCN, Marker marker, Level level, String msg, Object param, Throwable t) {
      FilterReply decision = this.loggerContext.getTurboFilterChainDecision_1(marker, this, level, msg, param, t);
      if (decision == FilterReply.NEUTRAL) {
         if (this.effectiveLevelInt > level.levelInt) {
            return;
         }
      } else if (decision == FilterReply.DENY) {
         return;
      }

      this.buildLoggingEventAndAppend(localFQCN, marker, level, msg, new Object[]{param}, t);
   }

   private void filterAndLog_2(String localFQCN, Marker marker, Level level, String msg, Object param1, Object param2, Throwable t) {
      FilterReply decision = this.loggerContext.getTurboFilterChainDecision_2(marker, this, level, msg, param1, param2, t);
      if (decision == FilterReply.NEUTRAL) {
         if (this.effectiveLevelInt > level.levelInt) {
            return;
         }
      } else if (decision == FilterReply.DENY) {
         return;
      }

      this.buildLoggingEventAndAppend(localFQCN, marker, level, msg, new Object[]{param1, param2}, t);
   }

   private void buildLoggingEventAndAppend(String localFQCN, Marker marker, Level level, String msg, Object[] params, Throwable t) {
      LoggingEvent le = new LoggingEvent(localFQCN, this, level, msg, t, params);
      le.setMarker(marker);
      this.callAppenders(le);
   }

   public void trace(String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.TRACE, msg, (Object[])null, (Throwable)null);
   }

   public void trace(String format, Object arg) {
      this.filterAndLog_1(FQCN, (Marker)null, Level.TRACE, format, arg, (Throwable)null);
   }

   public void trace(String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, (Marker)null, Level.TRACE, format, arg1, arg2, (Throwable)null);
   }

   public void trace(String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.TRACE, format, argArray, (Throwable)null);
   }

   public void trace(String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.TRACE, msg, (Object[])null, t);
   }

   public void trace(Marker marker, String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, msg, (Object[])null, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object arg) {
      this.filterAndLog_1(FQCN, marker, Level.TRACE, format, arg, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, marker, Level.TRACE, format, arg1, arg2, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, format, argArray, (Throwable)null);
   }

   public void trace(Marker marker, String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, msg, (Object[])null, t);
   }

   public boolean isDebugEnabled() {
      return this.isDebugEnabled((Marker)null);
   }

   public boolean isDebugEnabled(Marker marker) {
      FilterReply decision = this.callTurboFilters(marker, Level.DEBUG);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= 10000;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public void debug(String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.DEBUG, msg, (Object[])null, (Throwable)null);
   }

   public void debug(String format, Object arg) {
      this.filterAndLog_1(FQCN, (Marker)null, Level.DEBUG, format, arg, (Throwable)null);
   }

   public void debug(String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, (Marker)null, Level.DEBUG, format, arg1, arg2, (Throwable)null);
   }

   public void debug(String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.DEBUG, format, argArray, (Throwable)null);
   }

   public void debug(String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.DEBUG, msg, (Object[])null, t);
   }

   public void debug(Marker marker, String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, msg, (Object[])null, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object arg) {
      this.filterAndLog_1(FQCN, marker, Level.DEBUG, format, arg, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, marker, Level.DEBUG, format, arg1, arg2, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, format, argArray, (Throwable)null);
   }

   public void debug(Marker marker, String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, msg, (Object[])null, t);
   }

   public void error(String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.ERROR, msg, (Object[])null, (Throwable)null);
   }

   public void error(String format, Object arg) {
      this.filterAndLog_1(FQCN, (Marker)null, Level.ERROR, format, arg, (Throwable)null);
   }

   public void error(String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, (Marker)null, Level.ERROR, format, arg1, arg2, (Throwable)null);
   }

   public void error(String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.ERROR, format, argArray, (Throwable)null);
   }

   public void error(String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.ERROR, msg, (Object[])null, t);
   }

   public void error(Marker marker, String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, msg, (Object[])null, (Throwable)null);
   }

   public void error(Marker marker, String format, Object arg) {
      this.filterAndLog_1(FQCN, marker, Level.ERROR, format, arg, (Throwable)null);
   }

   public void error(Marker marker, String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, marker, Level.ERROR, format, arg1, arg2, (Throwable)null);
   }

   public void error(Marker marker, String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, format, argArray, (Throwable)null);
   }

   public void error(Marker marker, String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, msg, (Object[])null, t);
   }

   public boolean isInfoEnabled() {
      return this.isInfoEnabled((Marker)null);
   }

   public boolean isInfoEnabled(Marker marker) {
      FilterReply decision = this.callTurboFilters(marker, Level.INFO);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= 20000;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public void info(String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.INFO, msg, (Object[])null, (Throwable)null);
   }

   public void info(String format, Object arg) {
      this.filterAndLog_1(FQCN, (Marker)null, Level.INFO, format, arg, (Throwable)null);
   }

   public void info(String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, (Marker)null, Level.INFO, format, arg1, arg2, (Throwable)null);
   }

   public void info(String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.INFO, format, argArray, (Throwable)null);
   }

   public void info(String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.INFO, msg, (Object[])null, t);
   }

   public void info(Marker marker, String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, msg, (Object[])null, (Throwable)null);
   }

   public void info(Marker marker, String format, Object arg) {
      this.filterAndLog_1(FQCN, marker, Level.INFO, format, arg, (Throwable)null);
   }

   public void info(Marker marker, String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, marker, Level.INFO, format, arg1, arg2, (Throwable)null);
   }

   public void info(Marker marker, String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, format, argArray, (Throwable)null);
   }

   public void info(Marker marker, String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, msg, (Object[])null, t);
   }

   public boolean isTraceEnabled() {
      return this.isTraceEnabled((Marker)null);
   }

   public boolean isTraceEnabled(Marker marker) {
      FilterReply decision = this.callTurboFilters(marker, Level.TRACE);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= 5000;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public boolean isErrorEnabled() {
      return this.isErrorEnabled((Marker)null);
   }

   public boolean isErrorEnabled(Marker marker) {
      FilterReply decision = this.callTurboFilters(marker, Level.ERROR);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= 40000;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public boolean isWarnEnabled() {
      return this.isWarnEnabled((Marker)null);
   }

   public boolean isWarnEnabled(Marker marker) {
      FilterReply decision = this.callTurboFilters(marker, Level.WARN);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= 30000;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public boolean isEnabledFor(Marker marker, Level level) {
      FilterReply decision = this.callTurboFilters(marker, level);
      if (decision == FilterReply.NEUTRAL) {
         return this.effectiveLevelInt <= level.levelInt;
      } else if (decision == FilterReply.DENY) {
         return false;
      } else if (decision == FilterReply.ACCEPT) {
         return true;
      } else {
         throw new IllegalStateException("Unknown FilterReply value: " + decision);
      }
   }

   public boolean isEnabledFor(Level level) {
      return this.isEnabledFor((Marker)null, level);
   }

   public void warn(String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.WARN, msg, (Object[])null, (Throwable)null);
   }

   public void warn(String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.WARN, msg, (Object[])null, t);
   }

   public void warn(String format, Object arg) {
      this.filterAndLog_1(FQCN, (Marker)null, Level.WARN, format, arg, (Throwable)null);
   }

   public void warn(String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, (Marker)null, Level.WARN, format, arg1, arg2, (Throwable)null);
   }

   public void warn(String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, (Marker)null, Level.WARN, format, argArray, (Throwable)null);
   }

   public void warn(Marker marker, String msg) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, msg, (Object[])null, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object arg) {
      this.filterAndLog_1(FQCN, marker, Level.WARN, format, arg, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object... argArray) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, format, argArray, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object arg1, Object arg2) {
      this.filterAndLog_2(FQCN, marker, Level.WARN, format, arg1, arg2, (Throwable)null);
   }

   public void warn(Marker marker, String msg, Throwable t) {
      this.filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, msg, (Object[])null, t);
   }

   public boolean isAdditive() {
      return this.additive;
   }

   public void setAdditive(boolean additive) {
      this.additive = additive;
   }

   public String toString() {
      return "Logger[" + this.name + "]";
   }

   private FilterReply callTurboFilters(Marker marker, Level level) {
      return this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level, (String)null, (Object[])null, (Throwable)null);
   }

   public LoggerContext getLoggerContext() {
      return this.loggerContext;
   }

   public void log(Marker marker, String fqcn, int levelInt, String message, Object[] argArray, Throwable t) {
      Level level = Level.fromLocationAwareLoggerInteger(levelInt);
      this.filterAndLog_0_Or3Plus(fqcn, marker, level, message, argArray, t);
   }

   public void log(org.slf4j.event.LoggingEvent slf4jEvent) {
      Level level = Level.fromLocationAwareLoggerInteger(slf4jEvent.getLevel().toInt());
      this.filterAndLog_0_Or3Plus(FQCN, slf4jEvent.getMarker(), level, slf4jEvent.getMessage(), slf4jEvent.getArgumentArray(), slf4jEvent.getThrowable());
   }

   protected Object readResolve() throws ObjectStreamException {
      return LoggerFactory.getLogger(this.getName());
   }
}
