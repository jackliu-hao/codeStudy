/*     */ package ch.qos.logback.classic.joran;
/*     */ 
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.status.StatusUtil;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReconfigureOnChangeTask
/*     */   extends ContextAwareBase
/*     */   implements Runnable
/*     */ {
/*     */   public static final String DETECTED_CHANGE_IN_CONFIGURATION_FILES = "Detected change in configuration files.";
/*     */   static final String RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION = "Re-registering previous fallback configuration once more as a fallback configuration point";
/*     */   static final String FALLING_BACK_TO_SAFE_CONFIGURATION = "Given previous errors, falling back to previously registered safe configuration.";
/*  26 */   long birthdate = System.currentTimeMillis();
/*     */   
/*     */   List<ReconfigureOnChangeTaskListener> listeners;
/*     */   
/*     */   void addListener(ReconfigureOnChangeTaskListener listener) {
/*  31 */     if (this.listeners == null)
/*  32 */       this.listeners = new ArrayList<ReconfigureOnChangeTaskListener>(); 
/*  33 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  38 */     fireEnteredRunMethod();
/*     */     
/*  40 */     ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
/*  41 */     if (configurationWatchList == null) {
/*  42 */       addWarn("Empty ConfigurationWatchList in context");
/*     */       
/*     */       return;
/*     */     } 
/*  46 */     List<File> filesToWatch = configurationWatchList.getCopyOfFileWatchList();
/*  47 */     if (filesToWatch == null || filesToWatch.isEmpty()) {
/*  48 */       addInfo("Empty watch file list. Disabling ");
/*     */       
/*     */       return;
/*     */     } 
/*  52 */     if (!configurationWatchList.changeDetected()) {
/*     */       return;
/*     */     }
/*     */     
/*  56 */     fireChangeDetected();
/*  57 */     URL mainConfigurationURL = configurationWatchList.getMainURL();
/*     */     
/*  59 */     addInfo("Detected change in configuration files.");
/*  60 */     addInfo("Will reset and reconfigure context named [" + this.context.getName() + "]");
/*     */     
/*  62 */     LoggerContext lc = (LoggerContext)this.context;
/*  63 */     if (mainConfigurationURL.toString().endsWith("xml")) {
/*  64 */       performXMLConfiguration(lc, mainConfigurationURL);
/*     */     }
/*  66 */     fireDoneReconfiguring();
/*     */   }
/*     */   
/*     */   private void fireEnteredRunMethod() {
/*  70 */     if (this.listeners == null) {
/*     */       return;
/*     */     }
/*  73 */     for (ReconfigureOnChangeTaskListener listener : this.listeners)
/*  74 */       listener.enteredRunMethod(); 
/*     */   }
/*     */   
/*     */   private void fireChangeDetected() {
/*  78 */     if (this.listeners == null) {
/*     */       return;
/*     */     }
/*  81 */     for (ReconfigureOnChangeTaskListener listener : this.listeners) {
/*  82 */       listener.changeDetected();
/*     */     }
/*     */   }
/*     */   
/*     */   private void fireDoneReconfiguring() {
/*  87 */     if (this.listeners == null) {
/*     */       return;
/*     */     }
/*  90 */     for (ReconfigureOnChangeTaskListener listener : this.listeners)
/*  91 */       listener.doneReconfiguring(); 
/*     */   }
/*     */   
/*     */   private void performXMLConfiguration(LoggerContext lc, URL mainConfigurationURL) {
/*  95 */     JoranConfigurator jc = new JoranConfigurator();
/*  96 */     jc.setContext(this.context);
/*  97 */     StatusUtil statusUtil = new StatusUtil(this.context);
/*  98 */     List<SaxEvent> eventList = jc.recallSafeConfiguration();
/*     */     
/* 100 */     URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
/* 101 */     lc.reset();
/* 102 */     long threshold = System.currentTimeMillis();
/*     */     try {
/* 104 */       jc.doConfigure(mainConfigurationURL);
/* 105 */       if (statusUtil.hasXMLParsingErrors(threshold)) {
/* 106 */         fallbackConfiguration(lc, eventList, mainURL);
/*     */       }
/* 108 */     } catch (JoranException e) {
/* 109 */       fallbackConfiguration(lc, eventList, mainURL);
/*     */     } 
/*     */   }
/*     */   
/*     */   private List<SaxEvent> removeIncludeEvents(List<SaxEvent> unsanitizedEventList) {
/* 114 */     List<SaxEvent> sanitizedEvents = new ArrayList<SaxEvent>();
/* 115 */     if (unsanitizedEventList == null) {
/* 116 */       return sanitizedEvents;
/*     */     }
/* 118 */     for (SaxEvent e : unsanitizedEventList) {
/* 119 */       if (!"include".equalsIgnoreCase(e.getLocalName())) {
/* 120 */         sanitizedEvents.add(e);
/*     */       }
/*     */     } 
/* 123 */     return sanitizedEvents;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
/* 130 */     List<SaxEvent> failsafeEvents = removeIncludeEvents(eventList);
/* 131 */     JoranConfigurator joranConfigurator = new JoranConfigurator();
/* 132 */     joranConfigurator.setContext(this.context);
/* 133 */     ConfigurationWatchList oldCWL = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
/* 134 */     ConfigurationWatchList newCWL = oldCWL.buildClone();
/*     */     
/* 136 */     if (failsafeEvents == null || failsafeEvents.isEmpty()) {
/* 137 */       addWarn("No previous configuration to fall back on.");
/*     */     } else {
/* 139 */       addWarn("Given previous errors, falling back to previously registered safe configuration.");
/*     */       try {
/* 141 */         lc.reset();
/* 142 */         ConfigurationWatchListUtil.registerConfigurationWatchList(this.context, newCWL);
/* 143 */         joranConfigurator.doConfigure(failsafeEvents);
/* 144 */         addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
/* 145 */         joranConfigurator.registerSafeConfiguration(eventList);
/*     */         
/* 147 */         addInfo("after registerSafeConfiguration: " + eventList);
/* 148 */       } catch (JoranException e) {
/* 149 */         addError("Unexpected exception thrown by a configuration considered safe.", (Throwable)e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 156 */     return "ReconfigureOnChangeTask(born:" + this.birthdate + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\ReconfigureOnChangeTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */