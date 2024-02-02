/*     */ package ch.qos.logback.classic.turbo;
/*     */ 
/*     */ import ch.qos.logback.classic.Level;
/*     */ import ch.qos.logback.classic.Logger;
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.joran.JoranConfigurator;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
/*     */ import ch.qos.logback.core.spi.FilterReply;
/*     */ import ch.qos.logback.core.status.StatusUtil;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.slf4j.Marker;
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
/*     */ public class ReconfigureOnChangeFilter
/*     */   extends TurboFilter
/*     */ {
/*     */   public static final long DEFAULT_REFRESH_PERIOD = 60000L;
/*  49 */   long refreshPeriod = 60000L;
/*     */   
/*     */   URL mainConfigurationURL;
/*     */   
/*     */   protected volatile long nextCheck;
/*     */   ConfigurationWatchList configurationWatchList;
/*     */   
/*     */   public void start() {
/*  57 */     this.configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
/*  58 */     if (this.configurationWatchList != null) {
/*  59 */       this.mainConfigurationURL = this.configurationWatchList.getMainURL();
/*  60 */       if (this.mainConfigurationURL == null) {
/*  61 */         addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
/*     */         return;
/*     */       } 
/*  64 */       List<File> watchList = this.configurationWatchList.getCopyOfFileWatchList();
/*  65 */       long inSeconds = this.refreshPeriod / 1000L;
/*  66 */       addInfo("Will scan for changes in [" + watchList + "] every " + inSeconds + " seconds. ");
/*  67 */       synchronized (this.configurationWatchList) {
/*  68 */         updateNextCheck(System.currentTimeMillis());
/*     */       } 
/*  70 */       super.start();
/*     */     } else {
/*  72 */       addWarn("Empty ConfigurationWatchList in context");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  78 */     return "ReconfigureOnChangeFilter{invocationCounter=" + this.invocationCounter + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   private long invocationCounter = 0L;
/*     */   
/*  89 */   private volatile long mask = 15L;
/*  90 */   private volatile long lastMaskCheck = System.currentTimeMillis();
/*     */   private static final int MAX_MASK = 65535;
/*     */   
/*     */   public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
/*  94 */     if (!isStarted()) {
/*  95 */       return FilterReply.NEUTRAL;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     if ((this.invocationCounter++ & this.mask) != this.mask) {
/* 102 */       return FilterReply.NEUTRAL;
/*     */     }
/*     */     
/* 105 */     long now = System.currentTimeMillis();
/*     */     
/* 107 */     synchronized (this.configurationWatchList) {
/* 108 */       updateMaskIfNecessary(now);
/* 109 */       if (changeDetected(now)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 114 */         disableSubsequentReconfiguration();
/* 115 */         detachReconfigurationToNewThread();
/*     */       } 
/*     */     } 
/*     */     
/* 119 */     return FilterReply.NEUTRAL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_INCREASE_THRESHOLD = 100L;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_DECREASE_THRESHOLD = 800L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateMaskIfNecessary(long now) {
/* 136 */     long timeElapsedSinceLastMaskUpdateCheck = now - this.lastMaskCheck;
/* 137 */     this.lastMaskCheck = now;
/* 138 */     if (timeElapsedSinceLastMaskUpdateCheck < 100L && this.mask < 65535L) {
/* 139 */       this.mask = this.mask << 1L | 0x1L;
/* 140 */     } else if (timeElapsedSinceLastMaskUpdateCheck > 800L) {
/* 141 */       this.mask >>>= 2L;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void detachReconfigurationToNewThread() {
/* 149 */     addInfo("Detected change in [" + this.configurationWatchList.getCopyOfFileWatchList() + "]");
/* 150 */     this.context.getExecutorService().submit(new ReconfiguringThread());
/*     */   }
/*     */   
/*     */   void updateNextCheck(long now) {
/* 154 */     this.nextCheck = now + this.refreshPeriod;
/*     */   }
/*     */   
/*     */   protected boolean changeDetected(long now) {
/* 158 */     if (now >= this.nextCheck) {
/* 159 */       updateNextCheck(now);
/* 160 */       return this.configurationWatchList.changeDetected();
/*     */     } 
/* 162 */     return false;
/*     */   }
/*     */   
/*     */   void disableSubsequentReconfiguration() {
/* 166 */     this.nextCheck = Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */   public long getRefreshPeriod() {
/* 170 */     return this.refreshPeriod;
/*     */   }
/*     */   
/*     */   public void setRefreshPeriod(long refreshPeriod) {
/* 174 */     this.refreshPeriod = refreshPeriod;
/*     */   }
/*     */   
/*     */   class ReconfiguringThread implements Runnable {
/*     */     public void run() {
/* 179 */       if (ReconfigureOnChangeFilter.this.mainConfigurationURL == null) {
/* 180 */         ReconfigureOnChangeFilter.this.addInfo("Due to missing top level configuration file, skipping reconfiguration");
/*     */         return;
/*     */       } 
/* 183 */       LoggerContext lc = (LoggerContext)ReconfigureOnChangeFilter.this.context;
/* 184 */       ReconfigureOnChangeFilter.this.addInfo("Will reset and reconfigure context named [" + ReconfigureOnChangeFilter.this.context.getName() + "]");
/* 185 */       if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("xml")) {
/* 186 */         performXMLConfiguration(lc);
/*     */       }
/*     */     }
/*     */     
/*     */     private void performXMLConfiguration(LoggerContext lc) {
/* 191 */       JoranConfigurator jc = new JoranConfigurator();
/* 192 */       jc.setContext(ReconfigureOnChangeFilter.this.context);
/* 193 */       StatusUtil statusUtil = new StatusUtil(ReconfigureOnChangeFilter.this.context);
/* 194 */       List<SaxEvent> eventList = jc.recallSafeConfiguration();
/* 195 */       URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(ReconfigureOnChangeFilter.this.context);
/* 196 */       lc.reset();
/* 197 */       long threshold = System.currentTimeMillis();
/*     */       try {
/* 199 */         jc.doConfigure(ReconfigureOnChangeFilter.this.mainConfigurationURL);
/* 200 */         if (statusUtil.hasXMLParsingErrors(threshold)) {
/* 201 */           fallbackConfiguration(lc, eventList, mainURL);
/*     */         }
/* 203 */       } catch (JoranException e) {
/* 204 */         fallbackConfiguration(lc, eventList, mainURL);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
/* 209 */       JoranConfigurator joranConfigurator = new JoranConfigurator();
/* 210 */       joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
/* 211 */       if (eventList != null) {
/* 212 */         ReconfigureOnChangeFilter.this.addWarn("Falling back to previously registered safe configuration.");
/*     */         try {
/* 214 */           lc.reset();
/* 215 */           JoranConfigurator.informContextOfURLUsedForConfiguration(ReconfigureOnChangeFilter.this.context, mainURL);
/* 216 */           joranConfigurator.doConfigure(eventList);
/* 217 */           ReconfigureOnChangeFilter.this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
/* 218 */           joranConfigurator.registerSafeConfiguration(eventList);
/* 219 */         } catch (JoranException e) {
/* 220 */           ReconfigureOnChangeFilter.this.addError("Unexpected exception thrown by a configuration considered safe.", (Throwable)e);
/*     */         } 
/*     */       } else {
/* 223 */         ReconfigureOnChangeFilter.this.addWarn("No previous configuration to fall back on.");
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\turbo\ReconfigureOnChangeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */