package ch.qos.logback.classic.jul;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LevelChangePropagator extends ContextAwareBase implements LoggerContextListener, LifeCycle {
   private Set<Logger> julLoggerSet = new HashSet();
   boolean isStarted = false;
   boolean resetJUL = false;

   public void setResetJUL(boolean resetJUL) {
      this.resetJUL = resetJUL;
   }

   public boolean isResetResistant() {
      return false;
   }

   public void onStart(LoggerContext context) {
   }

   public void onReset(LoggerContext context) {
   }

   public void onStop(LoggerContext context) {
   }

   public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
      this.propagate(logger, level);
   }

   private void propagate(ch.qos.logback.classic.Logger logger, Level level) {
      this.addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
      Logger julLogger = JULHelper.asJULLogger(logger);
      this.julLoggerSet.add(julLogger);
      java.util.logging.Level julLevel = JULHelper.asJULLevel(level);
      julLogger.setLevel(julLevel);
   }

   public void resetJULLevels() {
      LogManager lm = LogManager.getLogManager();
      Enumeration<String> e = lm.getLoggerNames();

      while(e.hasMoreElements()) {
         String loggerName = (String)e.nextElement();
         Logger julLogger = lm.getLogger(loggerName);
         if (JULHelper.isRegularNonRootLogger(julLogger) && julLogger.getLevel() != null) {
            this.addInfo("Setting level of jul logger [" + loggerName + "] to null");
            julLogger.setLevel((java.util.logging.Level)null);
         }
      }

   }

   private void propagateExistingLoggerLevels() {
      LoggerContext loggerContext = (LoggerContext)this.context;
      List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
      Iterator var3 = loggerList.iterator();

      while(var3.hasNext()) {
         ch.qos.logback.classic.Logger l = (ch.qos.logback.classic.Logger)var3.next();
         if (l.getLevel() != null) {
            this.propagate(l, l.getLevel());
         }
      }

   }

   public void start() {
      if (this.resetJUL) {
         this.resetJULLevels();
      }

      this.propagateExistingLoggerLevels();
      this.isStarted = true;
   }

   public void stop() {
      this.isStarted = false;
   }

   public boolean isStarted() {
      return this.isStarted;
   }
}
