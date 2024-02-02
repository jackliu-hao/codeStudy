package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.slf4j.Marker;

public class ReconfigureOnChangeFilter extends TurboFilter {
   public static final long DEFAULT_REFRESH_PERIOD = 60000L;
   long refreshPeriod = 60000L;
   URL mainConfigurationURL;
   protected volatile long nextCheck;
   ConfigurationWatchList configurationWatchList;
   private long invocationCounter = 0L;
   private volatile long mask = 15L;
   private volatile long lastMaskCheck = System.currentTimeMillis();
   private static final int MAX_MASK = 65535;
   private static final long MASK_INCREASE_THRESHOLD = 100L;
   private static final long MASK_DECREASE_THRESHOLD = 800L;

   public void start() {
      this.configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
      if (this.configurationWatchList != null) {
         this.mainConfigurationURL = this.configurationWatchList.getMainURL();
         if (this.mainConfigurationURL == null) {
            this.addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
            return;
         }

         List<File> watchList = this.configurationWatchList.getCopyOfFileWatchList();
         long inSeconds = this.refreshPeriod / 1000L;
         this.addInfo("Will scan for changes in [" + watchList + "] every " + inSeconds + " seconds. ");
         synchronized(this.configurationWatchList) {
            this.updateNextCheck(System.currentTimeMillis());
         }

         super.start();
      } else {
         this.addWarn("Empty ConfigurationWatchList in context");
      }

   }

   public String toString() {
      return "ReconfigureOnChangeFilter{invocationCounter=" + this.invocationCounter + '}';
   }

   public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      if (!this.isStarted()) {
         return FilterReply.NEUTRAL;
      } else if ((this.invocationCounter++ & this.mask) != this.mask) {
         return FilterReply.NEUTRAL;
      } else {
         long now = System.currentTimeMillis();
         synchronized(this.configurationWatchList) {
            this.updateMaskIfNecessary(now);
            if (this.changeDetected(now)) {
               this.disableSubsequentReconfiguration();
               this.detachReconfigurationToNewThread();
            }
         }

         return FilterReply.NEUTRAL;
      }
   }

   private void updateMaskIfNecessary(long now) {
      long timeElapsedSinceLastMaskUpdateCheck = now - this.lastMaskCheck;
      this.lastMaskCheck = now;
      if (timeElapsedSinceLastMaskUpdateCheck < 100L && this.mask < 65535L) {
         this.mask = this.mask << 1 | 1L;
      } else if (timeElapsedSinceLastMaskUpdateCheck > 800L) {
         this.mask >>>= 2;
      }

   }

   void detachReconfigurationToNewThread() {
      this.addInfo("Detected change in [" + this.configurationWatchList.getCopyOfFileWatchList() + "]");
      this.context.getExecutorService().submit(new ReconfiguringThread());
   }

   void updateNextCheck(long now) {
      this.nextCheck = now + this.refreshPeriod;
   }

   protected boolean changeDetected(long now) {
      if (now >= this.nextCheck) {
         this.updateNextCheck(now);
         return this.configurationWatchList.changeDetected();
      } else {
         return false;
      }
   }

   void disableSubsequentReconfiguration() {
      this.nextCheck = Long.MAX_VALUE;
   }

   public long getRefreshPeriod() {
      return this.refreshPeriod;
   }

   public void setRefreshPeriod(long refreshPeriod) {
      this.refreshPeriod = refreshPeriod;
   }

   class ReconfiguringThread implements Runnable {
      public void run() {
         if (ReconfigureOnChangeFilter.this.mainConfigurationURL == null) {
            ReconfigureOnChangeFilter.this.addInfo("Due to missing top level configuration file, skipping reconfiguration");
         } else {
            LoggerContext lc = (LoggerContext)ReconfigureOnChangeFilter.this.context;
            ReconfigureOnChangeFilter.this.addInfo("Will reset and reconfigure context named [" + ReconfigureOnChangeFilter.this.context.getName() + "]");
            if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("xml")) {
               this.performXMLConfiguration(lc);
            }

         }
      }

      private void performXMLConfiguration(LoggerContext lc) {
         JoranConfigurator jc = new JoranConfigurator();
         jc.setContext(ReconfigureOnChangeFilter.this.context);
         StatusUtil statusUtil = new StatusUtil(ReconfigureOnChangeFilter.this.context);
         List<SaxEvent> eventList = jc.recallSafeConfiguration();
         URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(ReconfigureOnChangeFilter.this.context);
         lc.reset();
         long threshold = System.currentTimeMillis();

         try {
            jc.doConfigure(ReconfigureOnChangeFilter.this.mainConfigurationURL);
            if (statusUtil.hasXMLParsingErrors(threshold)) {
               this.fallbackConfiguration(lc, eventList, mainURL);
            }
         } catch (JoranException var9) {
            this.fallbackConfiguration(lc, eventList, mainURL);
         }

      }

      private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
         JoranConfigurator joranConfigurator = new JoranConfigurator();
         joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
         if (eventList != null) {
            ReconfigureOnChangeFilter.this.addWarn("Falling back to previously registered safe configuration.");

            try {
               lc.reset();
               JoranConfigurator.informContextOfURLUsedForConfiguration(ReconfigureOnChangeFilter.this.context, mainURL);
               joranConfigurator.doConfigure(eventList);
               ReconfigureOnChangeFilter.this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
               joranConfigurator.registerSafeConfiguration(eventList);
            } catch (JoranException var6) {
               ReconfigureOnChangeFilter.this.addError("Unexpected exception thrown by a configuration considered safe.", var6);
            }
         } else {
            ReconfigureOnChangeFilter.this.addWarn("No previous configuration to fall back on.");
         }

      }
   }
}
