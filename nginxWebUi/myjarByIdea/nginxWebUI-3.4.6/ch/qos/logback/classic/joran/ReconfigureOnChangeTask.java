package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReconfigureOnChangeTask extends ContextAwareBase implements Runnable {
   public static final String DETECTED_CHANGE_IN_CONFIGURATION_FILES = "Detected change in configuration files.";
   static final String RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION = "Re-registering previous fallback configuration once more as a fallback configuration point";
   static final String FALLING_BACK_TO_SAFE_CONFIGURATION = "Given previous errors, falling back to previously registered safe configuration.";
   long birthdate = System.currentTimeMillis();
   List<ReconfigureOnChangeTaskListener> listeners;

   void addListener(ReconfigureOnChangeTaskListener listener) {
      if (this.listeners == null) {
         this.listeners = new ArrayList();
      }

      this.listeners.add(listener);
   }

   public void run() {
      this.fireEnteredRunMethod();
      ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
      if (configurationWatchList == null) {
         this.addWarn("Empty ConfigurationWatchList in context");
      } else {
         List<File> filesToWatch = configurationWatchList.getCopyOfFileWatchList();
         if (filesToWatch != null && !filesToWatch.isEmpty()) {
            if (configurationWatchList.changeDetected()) {
               this.fireChangeDetected();
               URL mainConfigurationURL = configurationWatchList.getMainURL();
               this.addInfo("Detected change in configuration files.");
               this.addInfo("Will reset and reconfigure context named [" + this.context.getName() + "]");
               LoggerContext lc = (LoggerContext)this.context;
               if (mainConfigurationURL.toString().endsWith("xml")) {
                  this.performXMLConfiguration(lc, mainConfigurationURL);
               }

               this.fireDoneReconfiguring();
            }
         } else {
            this.addInfo("Empty watch file list. Disabling ");
         }
      }
   }

   private void fireEnteredRunMethod() {
      if (this.listeners != null) {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            ReconfigureOnChangeTaskListener listener = (ReconfigureOnChangeTaskListener)var1.next();
            listener.enteredRunMethod();
         }

      }
   }

   private void fireChangeDetected() {
      if (this.listeners != null) {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            ReconfigureOnChangeTaskListener listener = (ReconfigureOnChangeTaskListener)var1.next();
            listener.changeDetected();
         }

      }
   }

   private void fireDoneReconfiguring() {
      if (this.listeners != null) {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            ReconfigureOnChangeTaskListener listener = (ReconfigureOnChangeTaskListener)var1.next();
            listener.doneReconfiguring();
         }

      }
   }

   private void performXMLConfiguration(LoggerContext lc, URL mainConfigurationURL) {
      JoranConfigurator jc = new JoranConfigurator();
      jc.setContext(this.context);
      StatusUtil statusUtil = new StatusUtil(this.context);
      List<SaxEvent> eventList = jc.recallSafeConfiguration();
      URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
      lc.reset();
      long threshold = System.currentTimeMillis();

      try {
         jc.doConfigure(mainConfigurationURL);
         if (statusUtil.hasXMLParsingErrors(threshold)) {
            this.fallbackConfiguration(lc, eventList, mainURL);
         }
      } catch (JoranException var10) {
         this.fallbackConfiguration(lc, eventList, mainURL);
      }

   }

   private List<SaxEvent> removeIncludeEvents(List<SaxEvent> unsanitizedEventList) {
      List<SaxEvent> sanitizedEvents = new ArrayList();
      if (unsanitizedEventList == null) {
         return sanitizedEvents;
      } else {
         Iterator var3 = unsanitizedEventList.iterator();

         while(var3.hasNext()) {
            SaxEvent e = (SaxEvent)var3.next();
            if (!"include".equalsIgnoreCase(e.getLocalName())) {
               sanitizedEvents.add(e);
            }
         }

         return sanitizedEvents;
      }
   }

   private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
      List<SaxEvent> failsafeEvents = this.removeIncludeEvents(eventList);
      JoranConfigurator joranConfigurator = new JoranConfigurator();
      joranConfigurator.setContext(this.context);
      ConfigurationWatchList oldCWL = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
      ConfigurationWatchList newCWL = oldCWL.buildClone();
      if (failsafeEvents != null && !failsafeEvents.isEmpty()) {
         this.addWarn("Given previous errors, falling back to previously registered safe configuration.");

         try {
            lc.reset();
            ConfigurationWatchListUtil.registerConfigurationWatchList(this.context, newCWL);
            joranConfigurator.doConfigure(failsafeEvents);
            this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
            joranConfigurator.registerSafeConfiguration(eventList);
            this.addInfo("after registerSafeConfiguration: " + eventList);
         } catch (JoranException var9) {
            this.addError("Unexpected exception thrown by a configuration considered safe.", var9);
         }
      } else {
         this.addWarn("No previous configuration to fall back on.");
      }

   }

   public String toString() {
      return "ReconfigureOnChangeTask(born:" + this.birthdate + ")";
   }
}
