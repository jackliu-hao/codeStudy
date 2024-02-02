package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.xml.sax.Attributes;

public class ConfigurationAction extends Action {
   static final String INTERNAL_DEBUG_ATTR = "debug";
   static final String PACKAGING_DATA_ATTR = "packagingData";
   static final String SCAN_ATTR = "scan";
   static final String SCAN_PERIOD_ATTR = "scanPeriod";
   static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
   long threshold = 0L;

   public void begin(InterpretationContext ic, String name, Attributes attributes) {
      this.threshold = System.currentTimeMillis();
      String debugAttrib = this.getSystemProperty("logback.debug");
      if (debugAttrib == null) {
         debugAttrib = ic.subst(attributes.getValue("debug"));
      }

      if (!OptionHelper.isEmpty(debugAttrib) && !debugAttrib.equalsIgnoreCase("false") && !debugAttrib.equalsIgnoreCase("null")) {
         StatusListenerConfigHelper.addOnConsoleListenerInstance(this.context, new OnConsoleStatusListener());
      } else {
         this.addInfo("debug attribute not set");
      }

      this.processScanAttrib(ic, attributes);
      LoggerContext lc = (LoggerContext)this.context;
      boolean packagingData = OptionHelper.toBoolean(ic.subst(attributes.getValue("packagingData")), false);
      lc.setPackagingDataEnabled(packagingData);
      if (EnvUtil.isGroovyAvailable()) {
         ContextUtil contextUtil = new ContextUtil(this.context);
         contextUtil.addGroovyPackages(lc.getFrameworkPackages());
      }

      ic.pushObject(this.getContext());
   }

   String getSystemProperty(String name) {
      try {
         return System.getProperty(name);
      } catch (SecurityException var3) {
         return null;
      }
   }

   void processScanAttrib(InterpretationContext ic, Attributes attributes) {
      String scanAttrib = ic.subst(attributes.getValue("scan"));
      if (!OptionHelper.isEmpty(scanAttrib) && !"false".equalsIgnoreCase(scanAttrib)) {
         ScheduledExecutorService scheduledExecutorService = this.context.getScheduledExecutorService();
         URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
         if (mainURL == null) {
            this.addWarn("Due to missing top level configuration file, reconfiguration on change (configuration file scanning) cannot be done.");
            return;
         }

         ReconfigureOnChangeTask rocTask = new ReconfigureOnChangeTask();
         rocTask.setContext(this.context);
         this.context.putObject("RECONFIGURE_ON_CHANGE_TASK", rocTask);
         String scanPeriodAttrib = ic.subst(attributes.getValue("scanPeriod"));
         Duration duration = this.getDuration(scanAttrib, scanPeriodAttrib);
         if (duration == null) {
            return;
         }

         this.addInfo("Will scan for changes in [" + mainURL + "] ");
         this.addInfo("Setting ReconfigureOnChangeTask scanning period to " + duration);
         ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(rocTask, duration.getMilliseconds(), duration.getMilliseconds(), TimeUnit.MILLISECONDS);
         this.context.addScheduledFuture(scheduledFuture);
      }

   }

   private Duration getDuration(String scanAttrib, String scanPeriodAttrib) {
      Duration duration = null;
      if (!OptionHelper.isEmpty(scanPeriodAttrib)) {
         try {
            duration = Duration.valueOf(scanPeriodAttrib);
         } catch (NumberFormatException var5) {
            this.addError("Error while converting [" + scanAttrib + "] to long", var5);
         }
      }

      return duration;
   }

   public void end(InterpretationContext ec, String name) {
      this.addInfo("End of configuration.");
      ec.popObject();
   }
}
