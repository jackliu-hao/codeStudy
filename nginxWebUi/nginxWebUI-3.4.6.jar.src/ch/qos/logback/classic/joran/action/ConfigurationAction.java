/*     */ package ch.qos.logback.classic.joran.action;
/*     */ 
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
/*     */ import ch.qos.logback.classic.util.EnvUtil;
/*     */ import ch.qos.logback.core.joran.action.Action;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
/*     */ import ch.qos.logback.core.status.OnConsoleStatusListener;
/*     */ import ch.qos.logback.core.util.ContextUtil;
/*     */ import ch.qos.logback.core.util.Duration;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import ch.qos.logback.core.util.StatusListenerConfigHelper;
/*     */ import java.net.URL;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigurationAction
/*     */   extends Action
/*     */ {
/*     */   static final String INTERNAL_DEBUG_ATTR = "debug";
/*     */   static final String PACKAGING_DATA_ATTR = "packagingData";
/*     */   static final String SCAN_ATTR = "scan";
/*     */   static final String SCAN_PERIOD_ATTR = "scanPeriod";
/*     */   static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
/*  43 */   long threshold = 0L;
/*     */   
/*     */   public void begin(InterpretationContext ic, String name, Attributes attributes) {
/*  46 */     this.threshold = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     String debugAttrib = getSystemProperty("logback.debug");
/*  52 */     if (debugAttrib == null) {
/*  53 */       debugAttrib = ic.subst(attributes.getValue("debug"));
/*     */     }
/*     */     
/*  56 */     if (OptionHelper.isEmpty(debugAttrib) || debugAttrib.equalsIgnoreCase("false") || debugAttrib.equalsIgnoreCase("null")) {
/*  57 */       addInfo("debug attribute not set");
/*     */     } else {
/*  59 */       StatusListenerConfigHelper.addOnConsoleListenerInstance(this.context, new OnConsoleStatusListener());
/*     */     } 
/*     */     
/*  62 */     processScanAttrib(ic, attributes);
/*     */     
/*  64 */     LoggerContext lc = (LoggerContext)this.context;
/*  65 */     boolean packagingData = OptionHelper.toBoolean(ic.subst(attributes.getValue("packagingData")), false);
/*  66 */     lc.setPackagingDataEnabled(packagingData);
/*     */     
/*  68 */     if (EnvUtil.isGroovyAvailable()) {
/*  69 */       ContextUtil contextUtil = new ContextUtil(this.context);
/*  70 */       contextUtil.addGroovyPackages(lc.getFrameworkPackages());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  75 */     ic.pushObject(getContext());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getSystemProperty(String name) {
/*     */     try {
/*  84 */       return System.getProperty(name);
/*  85 */     } catch (SecurityException ex) {
/*  86 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   void processScanAttrib(InterpretationContext ic, Attributes attributes) {
/*  91 */     String scanAttrib = ic.subst(attributes.getValue("scan"));
/*  92 */     if (!OptionHelper.isEmpty(scanAttrib) && !"false".equalsIgnoreCase(scanAttrib)) {
/*     */       
/*  94 */       ScheduledExecutorService scheduledExecutorService = this.context.getScheduledExecutorService();
/*  95 */       URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
/*  96 */       if (mainURL == null) {
/*  97 */         addWarn("Due to missing top level configuration file, reconfiguration on change (configuration file scanning) cannot be done.");
/*     */         return;
/*     */       } 
/* 100 */       ReconfigureOnChangeTask rocTask = new ReconfigureOnChangeTask();
/* 101 */       rocTask.setContext(this.context);
/*     */       
/* 103 */       this.context.putObject("RECONFIGURE_ON_CHANGE_TASK", rocTask);
/*     */       
/* 105 */       String scanPeriodAttrib = ic.subst(attributes.getValue("scanPeriod"));
/* 106 */       Duration duration = getDuration(scanAttrib, scanPeriodAttrib);
/*     */       
/* 108 */       if (duration == null) {
/*     */         return;
/*     */       }
/*     */       
/* 112 */       addInfo("Will scan for changes in [" + mainURL + "] ");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 117 */       addInfo("Setting ReconfigureOnChangeTask scanning period to " + duration);
/*     */       
/* 119 */       ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate((Runnable)rocTask, duration.getMilliseconds(), duration.getMilliseconds(), TimeUnit.MILLISECONDS);
/*     */       
/* 121 */       this.context.addScheduledFuture(scheduledFuture);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Duration getDuration(String scanAttrib, String scanPeriodAttrib) {
/* 126 */     Duration duration = null;
/*     */     
/* 128 */     if (!OptionHelper.isEmpty(scanPeriodAttrib)) {
/*     */       try {
/* 130 */         duration = Duration.valueOf(scanPeriodAttrib);
/*     */       }
/* 132 */       catch (NumberFormatException nfe) {
/* 133 */         addError("Error while converting [" + scanAttrib + "] to long", nfe);
/*     */       } 
/*     */     }
/* 136 */     return duration;
/*     */   }
/*     */   
/*     */   public void end(InterpretationContext ec, String name) {
/* 140 */     addInfo("End of configuration.");
/* 141 */     ec.popObject();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\ConfigurationAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */