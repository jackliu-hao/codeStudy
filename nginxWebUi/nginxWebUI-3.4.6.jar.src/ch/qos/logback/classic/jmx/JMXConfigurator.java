/*     */ package ch.qos.logback.classic.jmx;
/*     */ 
/*     */ import ch.qos.logback.classic.Level;
/*     */ import ch.qos.logback.classic.Logger;
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.joran.JoranConfigurator;
/*     */ import ch.qos.logback.classic.spi.LoggerContextListener;
/*     */ import ch.qos.logback.classic.util.ContextInitializer;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusListener;
/*     */ import ch.qos.logback.core.status.StatusListenerAsList;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.util.StatusPrinter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.MBeanRegistrationException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
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
/*     */ public class JMXConfigurator
/*     */   extends ContextAwareBase
/*     */   implements JMXConfiguratorMBean, LoggerContextListener
/*     */ {
/*  56 */   private static String EMPTY = "";
/*     */   
/*     */   LoggerContext loggerContext;
/*     */   
/*     */   MBeanServer mbs;
/*     */   
/*     */   ObjectName objectName;
/*     */   
/*     */   String objectNameAsString;
/*     */   
/*     */   boolean debug = true;
/*     */   boolean started;
/*     */   
/*     */   public JMXConfigurator(LoggerContext loggerContext, MBeanServer mbs, ObjectName objectName) {
/*  70 */     this.started = true;
/*  71 */     this.context = (Context)loggerContext;
/*  72 */     this.loggerContext = loggerContext;
/*  73 */     this.mbs = mbs;
/*  74 */     this.objectName = objectName;
/*  75 */     this.objectNameAsString = objectName.toString();
/*  76 */     if (previouslyRegisteredListenerWithSameObjectName()) {
/*  77 */       addError("Previously registered JMXConfigurator named [" + this.objectNameAsString + "] in the logger context named [" + loggerContext.getName() + "]");
/*     */     } else {
/*     */       
/*  80 */       loggerContext.addListener(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean previouslyRegisteredListenerWithSameObjectName() {
/*  85 */     List<LoggerContextListener> lcll = this.loggerContext.getCopyOfListenerList();
/*  86 */     for (LoggerContextListener lcl : lcll) {
/*  87 */       if (lcl instanceof JMXConfigurator) {
/*  88 */         JMXConfigurator jmxConfigurator = (JMXConfigurator)lcl;
/*  89 */         if (this.objectName.equals(jmxConfigurator.objectName)) {
/*  90 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*  94 */     return false;
/*     */   }
/*     */   
/*     */   public void reloadDefaultConfiguration() throws JoranException {
/*  98 */     ContextInitializer ci = new ContextInitializer(this.loggerContext);
/*  99 */     URL url = ci.findURLOfDefaultConfigurationFile(true);
/* 100 */     reloadByURL(url);
/*     */   }
/*     */   
/*     */   public void reloadByFileName(String fileName) throws JoranException, FileNotFoundException {
/* 104 */     File f = new File(fileName);
/* 105 */     if (f.exists() && f.isFile()) {
/*     */       
/*     */       try {
/* 108 */         URL url = f.toURI().toURL();
/* 109 */         reloadByURL(url);
/* 110 */       } catch (MalformedURLException e) {
/* 111 */         throw new RuntimeException("Unexpected MalformedURLException occured. See nexted cause.", e);
/*     */       } 
/*     */     } else {
/*     */       
/* 115 */       String errMsg = "Could not find [" + fileName + "]";
/* 116 */       addInfo(errMsg);
/* 117 */       throw new FileNotFoundException(errMsg);
/*     */     } 
/*     */   }
/*     */   
/*     */   void addStatusListener(StatusListener statusListener) {
/* 122 */     StatusManager sm = this.loggerContext.getStatusManager();
/* 123 */     sm.add(statusListener);
/*     */   }
/*     */   
/*     */   void removeStatusListener(StatusListener statusListener) {
/* 127 */     StatusManager sm = this.loggerContext.getStatusManager();
/* 128 */     sm.remove(statusListener);
/*     */   }
/*     */   
/*     */   public void reloadByURL(URL url) throws JoranException {
/* 132 */     StatusListenerAsList statusListenerAsList = new StatusListenerAsList();
/*     */     
/* 134 */     addStatusListener((StatusListener)statusListenerAsList);
/* 135 */     addInfo("Resetting context: " + this.loggerContext.getName());
/* 136 */     this.loggerContext.reset();
/*     */ 
/*     */     
/* 139 */     addStatusListener((StatusListener)statusListenerAsList);
/*     */     
/*     */     try {
/* 142 */       if (url != null) {
/* 143 */         JoranConfigurator configurator = new JoranConfigurator();
/* 144 */         configurator.setContext((Context)this.loggerContext);
/* 145 */         configurator.doConfigure(url);
/* 146 */         addInfo("Context: " + this.loggerContext.getName() + " reloaded.");
/*     */       } 
/*     */     } finally {
/* 149 */       removeStatusListener((StatusListener)statusListenerAsList);
/* 150 */       if (this.debug) {
/* 151 */         StatusPrinter.print(statusListenerAsList.getStatusList());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setLoggerLevel(String loggerName, String levelStr) {
/* 157 */     if (loggerName == null) {
/*     */       return;
/*     */     }
/* 160 */     if (levelStr == null) {
/*     */       return;
/*     */     }
/* 163 */     loggerName = loggerName.trim();
/* 164 */     levelStr = levelStr.trim();
/*     */     
/* 166 */     addInfo("Trying to set level " + levelStr + " to logger " + loggerName);
/* 167 */     LoggerContext lc = (LoggerContext)this.context;
/*     */     
/* 169 */     Logger logger = lc.getLogger(loggerName);
/* 170 */     if ("null".equalsIgnoreCase(levelStr)) {
/* 171 */       logger.setLevel(null);
/*     */     } else {
/* 173 */       Level level = Level.toLevel(levelStr, null);
/* 174 */       if (level != null) {
/* 175 */         logger.setLevel(level);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getLoggerLevel(String loggerName) {
/* 181 */     if (loggerName == null) {
/* 182 */       return EMPTY;
/*     */     }
/*     */     
/* 185 */     loggerName = loggerName.trim();
/*     */     
/* 187 */     LoggerContext lc = (LoggerContext)this.context;
/* 188 */     Logger logger = lc.exists(loggerName);
/* 189 */     if (logger != null && logger.getLevel() != null) {
/* 190 */       return logger.getLevel().toString();
/*     */     }
/* 192 */     return EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLoggerEffectiveLevel(String loggerName) {
/* 197 */     if (loggerName == null) {
/* 198 */       return EMPTY;
/*     */     }
/*     */     
/* 201 */     loggerName = loggerName.trim();
/*     */     
/* 203 */     LoggerContext lc = (LoggerContext)this.context;
/* 204 */     Logger logger = lc.exists(loggerName);
/* 205 */     if (logger != null) {
/* 206 */       return logger.getEffectiveLevel().toString();
/*     */     }
/* 208 */     return EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getLoggerList() {
/* 213 */     LoggerContext lc = (LoggerContext)this.context;
/* 214 */     List<String> strList = new ArrayList<String>();
/* 215 */     Iterator<Logger> it = lc.getLoggerList().iterator();
/* 216 */     while (it.hasNext()) {
/* 217 */       Logger log = it.next();
/* 218 */       strList.add(log.getName());
/*     */     } 
/* 220 */     return strList;
/*     */   }
/*     */   
/*     */   public List<String> getStatuses() {
/* 224 */     List<String> list = new ArrayList<String>();
/* 225 */     Iterator<Status> it = this.context.getStatusManager().getCopyOfStatusList().iterator();
/* 226 */     while (it.hasNext()) {
/* 227 */       list.add(((Status)it.next()).toString());
/*     */     }
/* 229 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onStop(LoggerContext context) {
/* 237 */     if (!this.started) {
/* 238 */       addInfo("onStop() method called on a stopped JMXActivator [" + this.objectNameAsString + "]");
/*     */       return;
/*     */     } 
/* 241 */     if (this.mbs.isRegistered(this.objectName)) {
/*     */       try {
/* 243 */         addInfo("Unregistering mbean [" + this.objectNameAsString + "]");
/* 244 */         this.mbs.unregisterMBean(this.objectName);
/* 245 */       } catch (InstanceNotFoundException e) {
/*     */         
/* 247 */         addError("Unable to find a verifiably registered mbean [" + this.objectNameAsString + "]", e);
/* 248 */       } catch (MBeanRegistrationException e) {
/* 249 */         addError("Failed to unregister [" + this.objectNameAsString + "]", e);
/*     */       } 
/*     */     } else {
/* 252 */       addInfo("mbean [" + this.objectNameAsString + "] was not in the mbean registry. This is OK.");
/*     */     } 
/* 254 */     stop();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onLevelChange(Logger logger, Level level) {}
/*     */ 
/*     */   
/*     */   public void onReset(LoggerContext context) {
/* 262 */     addInfo("onReset() method called JMXActivator [" + this.objectNameAsString + "]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isResetResistant() {
/* 271 */     return true;
/*     */   }
/*     */   
/*     */   private void clearFields() {
/* 275 */     this.mbs = null;
/* 276 */     this.objectName = null;
/* 277 */     this.loggerContext = null;
/*     */   }
/*     */   
/*     */   private void stop() {
/* 281 */     this.started = false;
/* 282 */     clearFields();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onStart(LoggerContext context) {}
/*     */ 
/*     */   
/*     */   public String toString() {
/* 291 */     return getClass().getName() + "(" + this.context.getName() + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\jmx\JMXConfigurator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */