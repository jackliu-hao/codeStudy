package ch.qos.logback.classic.jmx;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusListenerAsList;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JMXConfigurator extends ContextAwareBase implements JMXConfiguratorMBean, LoggerContextListener {
   private static String EMPTY = "";
   LoggerContext loggerContext;
   MBeanServer mbs;
   ObjectName objectName;
   String objectNameAsString;
   boolean debug = true;
   boolean started = true;

   public JMXConfigurator(LoggerContext loggerContext, MBeanServer mbs, ObjectName objectName) {
      this.context = loggerContext;
      this.loggerContext = loggerContext;
      this.mbs = mbs;
      this.objectName = objectName;
      this.objectNameAsString = objectName.toString();
      if (this.previouslyRegisteredListenerWithSameObjectName()) {
         this.addError("Previously registered JMXConfigurator named [" + this.objectNameAsString + "] in the logger context named [" + loggerContext.getName() + "]");
      } else {
         loggerContext.addListener(this);
      }

   }

   private boolean previouslyRegisteredListenerWithSameObjectName() {
      List<LoggerContextListener> lcll = this.loggerContext.getCopyOfListenerList();
      Iterator var2 = lcll.iterator();

      while(var2.hasNext()) {
         LoggerContextListener lcl = (LoggerContextListener)var2.next();
         if (lcl instanceof JMXConfigurator) {
            JMXConfigurator jmxConfigurator = (JMXConfigurator)lcl;
            if (this.objectName.equals(jmxConfigurator.objectName)) {
               return true;
            }
         }
      }

      return false;
   }

   public void reloadDefaultConfiguration() throws JoranException {
      ContextInitializer ci = new ContextInitializer(this.loggerContext);
      URL url = ci.findURLOfDefaultConfigurationFile(true);
      this.reloadByURL(url);
   }

   public void reloadByFileName(String fileName) throws JoranException, FileNotFoundException {
      File f = new File(fileName);
      if (f.exists() && f.isFile()) {
         try {
            URL url = f.toURI().toURL();
            this.reloadByURL(url);
         } catch (MalformedURLException var5) {
            throw new RuntimeException("Unexpected MalformedURLException occured. See nexted cause.", var5);
         }
      } else {
         String errMsg = "Could not find [" + fileName + "]";
         this.addInfo(errMsg);
         throw new FileNotFoundException(errMsg);
      }
   }

   void addStatusListener(StatusListener statusListener) {
      StatusManager sm = this.loggerContext.getStatusManager();
      sm.add(statusListener);
   }

   void removeStatusListener(StatusListener statusListener) {
      StatusManager sm = this.loggerContext.getStatusManager();
      sm.remove(statusListener);
   }

   public void reloadByURL(URL url) throws JoranException {
      StatusListenerAsList statusListenerAsList = new StatusListenerAsList();
      this.addStatusListener(statusListenerAsList);
      this.addInfo("Resetting context: " + this.loggerContext.getName());
      this.loggerContext.reset();
      this.addStatusListener(statusListenerAsList);

      try {
         if (url != null) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(this.loggerContext);
            configurator.doConfigure(url);
            this.addInfo("Context: " + this.loggerContext.getName() + " reloaded.");
         }
      } finally {
         this.removeStatusListener(statusListenerAsList);
         if (this.debug) {
            StatusPrinter.print(statusListenerAsList.getStatusList());
         }

      }

   }

   public void setLoggerLevel(String loggerName, String levelStr) {
      if (loggerName != null) {
         if (levelStr != null) {
            loggerName = loggerName.trim();
            levelStr = levelStr.trim();
            this.addInfo("Trying to set level " + levelStr + " to logger " + loggerName);
            LoggerContext lc = (LoggerContext)this.context;
            Logger logger = lc.getLogger(loggerName);
            if ("null".equalsIgnoreCase(levelStr)) {
               logger.setLevel((Level)null);
            } else {
               Level level = Level.toLevel(levelStr, (Level)null);
               if (level != null) {
                  logger.setLevel(level);
               }
            }

         }
      }
   }

   public String getLoggerLevel(String loggerName) {
      if (loggerName == null) {
         return EMPTY;
      } else {
         loggerName = loggerName.trim();
         LoggerContext lc = (LoggerContext)this.context;
         Logger logger = lc.exists(loggerName);
         return logger != null && logger.getLevel() != null ? logger.getLevel().toString() : EMPTY;
      }
   }

   public String getLoggerEffectiveLevel(String loggerName) {
      if (loggerName == null) {
         return EMPTY;
      } else {
         loggerName = loggerName.trim();
         LoggerContext lc = (LoggerContext)this.context;
         Logger logger = lc.exists(loggerName);
         return logger != null ? logger.getEffectiveLevel().toString() : EMPTY;
      }
   }

   public List<String> getLoggerList() {
      LoggerContext lc = (LoggerContext)this.context;
      List<String> strList = new ArrayList();
      Iterator<Logger> it = lc.getLoggerList().iterator();

      while(it.hasNext()) {
         Logger log = (Logger)it.next();
         strList.add(log.getName());
      }

      return strList;
   }

   public List<String> getStatuses() {
      List<String> list = new ArrayList();
      Iterator<Status> it = this.context.getStatusManager().getCopyOfStatusList().iterator();

      while(it.hasNext()) {
         list.add(((Status)it.next()).toString());
      }

      return list;
   }

   public void onStop(LoggerContext context) {
      if (!this.started) {
         this.addInfo("onStop() method called on a stopped JMXActivator [" + this.objectNameAsString + "]");
      } else {
         if (this.mbs.isRegistered(this.objectName)) {
            try {
               this.addInfo("Unregistering mbean [" + this.objectNameAsString + "]");
               this.mbs.unregisterMBean(this.objectName);
            } catch (InstanceNotFoundException var3) {
               this.addError("Unable to find a verifiably registered mbean [" + this.objectNameAsString + "]", var3);
            } catch (MBeanRegistrationException var4) {
               this.addError("Failed to unregister [" + this.objectNameAsString + "]", var4);
            }
         } else {
            this.addInfo("mbean [" + this.objectNameAsString + "] was not in the mbean registry. This is OK.");
         }

         this.stop();
      }
   }

   public void onLevelChange(Logger logger, Level level) {
   }

   public void onReset(LoggerContext context) {
      this.addInfo("onReset() method called JMXActivator [" + this.objectNameAsString + "]");
   }

   public boolean isResetResistant() {
      return true;
   }

   private void clearFields() {
      this.mbs = null;
      this.objectName = null;
      this.loggerContext = null;
   }

   private void stop() {
      this.started = false;
      this.clearFields();
   }

   public void onStart(LoggerContext context) {
   }

   public String toString() {
      return this.getClass().getName() + "(" + this.context.getName() + ")";
   }
}
