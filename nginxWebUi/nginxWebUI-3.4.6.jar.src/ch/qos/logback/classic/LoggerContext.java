/*     */ package ch.qos.logback.classic;
/*     */ 
/*     */ import ch.qos.logback.classic.spi.LoggerComparator;
/*     */ import ch.qos.logback.classic.spi.LoggerContextListener;
/*     */ import ch.qos.logback.classic.spi.LoggerContextVO;
/*     */ import ch.qos.logback.classic.spi.TurboFilterList;
/*     */ import ch.qos.logback.classic.turbo.TurboFilter;
/*     */ import ch.qos.logback.classic.util.LoggerNameUtil;
/*     */ import ch.qos.logback.core.ContextBase;
/*     */ import ch.qos.logback.core.spi.FilterReply;
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusListener;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.status.WarnStatus;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import org.slf4j.ILoggerFactory;
/*     */ import org.slf4j.Logger;
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
/*     */ public class LoggerContext
/*     */   extends ContextBase
/*     */   implements ILoggerFactory, LifeCycle
/*     */ {
/*     */   public static final boolean DEFAULT_PACKAGING_DATA = false;
/*     */   final Logger root;
/*     */   private int size;
/*  60 */   private int noAppenderWarning = 0;
/*  61 */   private final List<LoggerContextListener> loggerContextListenerList = new ArrayList<LoggerContextListener>();
/*     */   
/*     */   private Map<String, Logger> loggerCache;
/*     */   
/*     */   private LoggerContextVO loggerContextRemoteView;
/*  66 */   private final TurboFilterList turboFilterList = new TurboFilterList();
/*     */   
/*     */   private boolean packagingDataEnabled = false;
/*  69 */   private int maxCallerDataDepth = 8;
/*     */   
/*  71 */   int resetCount = 0;
/*     */   
/*     */   private List<String> frameworkPackages;
/*     */   
/*     */   public LoggerContext() {
/*  76 */     this.loggerCache = new ConcurrentHashMap<String, Logger>();
/*     */     
/*  78 */     this.loggerContextRemoteView = new LoggerContextVO(this);
/*  79 */     this.root = new Logger("ROOT", null, this);
/*  80 */     this.root.setLevel(Level.DEBUG);
/*  81 */     this.loggerCache.put("ROOT", this.root);
/*  82 */     initEvaluatorMap();
/*  83 */     this.size = 1;
/*  84 */     this.frameworkPackages = new ArrayList<String>();
/*     */   }
/*     */   
/*     */   void initEvaluatorMap() {
/*  88 */     putObject("EVALUATOR_MAP", new HashMap<Object, Object>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateLoggerContextVO() {
/*  96 */     this.loggerContextRemoteView = new LoggerContextVO(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putProperty(String key, String val) {
/* 101 */     super.putProperty(key, val);
/* 102 */     updateLoggerContextVO();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 107 */     super.setName(name);
/* 108 */     updateLoggerContextVO();
/*     */   }
/*     */   
/*     */   public final Logger getLogger(Class<?> clazz) {
/* 112 */     return getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final Logger getLogger(String name) {
/* 118 */     if (name == null) {
/* 119 */       throw new IllegalArgumentException("name argument cannot be null");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 124 */     if ("ROOT".equalsIgnoreCase(name)) {
/* 125 */       return this.root;
/*     */     }
/*     */     
/* 128 */     int i = 0;
/* 129 */     Logger logger = this.root;
/*     */ 
/*     */ 
/*     */     
/* 133 */     Logger childLogger = this.loggerCache.get(name);
/*     */     
/* 135 */     if (childLogger != null) {
/* 136 */       return childLogger;
/*     */     }
/*     */ 
/*     */     
/*     */     while (true) {
/*     */       String childName;
/*     */       
/* 143 */       int h = LoggerNameUtil.getSeparatorIndexOf(name, i);
/* 144 */       if (h == -1) {
/* 145 */         childName = name;
/*     */       } else {
/* 147 */         childName = name.substring(0, h);
/*     */       } 
/*     */       
/* 150 */       i = h + 1;
/* 151 */       synchronized (logger) {
/* 152 */         childLogger = logger.getChildByName(childName);
/* 153 */         if (childLogger == null) {
/* 154 */           childLogger = logger.createChildByName(childName);
/* 155 */           this.loggerCache.put(childName, childLogger);
/* 156 */           incSize();
/*     */         } 
/*     */       } 
/* 159 */       logger = childLogger;
/* 160 */       if (h == -1) {
/* 161 */         return childLogger;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void incSize() {
/* 167 */     this.size++;
/*     */   }
/*     */   
/*     */   int size() {
/* 171 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger exists(String name) {
/* 181 */     return this.loggerCache.get(name);
/*     */   }
/*     */   
/*     */   final void noAppenderDefinedWarning(Logger logger) {
/* 185 */     if (this.noAppenderWarning++ == 0) {
/* 186 */       getStatusManager().add((Status)new WarnStatus("No appenders present in context [" + getName() + "] for logger [" + logger.getName() + "].", logger));
/*     */     }
/*     */   }
/*     */   
/*     */   public List<Logger> getLoggerList() {
/* 191 */     Collection<Logger> collection = this.loggerCache.values();
/* 192 */     List<Logger> loggerList = new ArrayList<Logger>(collection);
/* 193 */     Collections.sort(loggerList, (Comparator<? super Logger>)new LoggerComparator());
/* 194 */     return loggerList;
/*     */   }
/*     */   
/*     */   public LoggerContextVO getLoggerContextRemoteView() {
/* 198 */     return this.loggerContextRemoteView;
/*     */   }
/*     */   
/*     */   public void setPackagingDataEnabled(boolean packagingDataEnabled) {
/* 202 */     this.packagingDataEnabled = packagingDataEnabled;
/*     */   }
/*     */   
/*     */   public boolean isPackagingDataEnabled() {
/* 206 */     return this.packagingDataEnabled;
/*     */   }
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
/*     */   public void reset() {
/* 219 */     this.resetCount++;
/* 220 */     super.reset();
/* 221 */     initEvaluatorMap();
/* 222 */     initCollisionMaps();
/* 223 */     this.root.recursiveReset();
/* 224 */     resetTurboFilterList();
/* 225 */     cancelScheduledTasks();
/* 226 */     fireOnReset();
/* 227 */     resetListenersExceptResetResistant();
/* 228 */     resetStatusListeners();
/*     */   }
/*     */   
/*     */   private void cancelScheduledTasks() {
/* 232 */     for (ScheduledFuture<?> sf : (Iterable<ScheduledFuture<?>>)this.scheduledFutures) {
/* 233 */       sf.cancel(false);
/*     */     }
/* 235 */     this.scheduledFutures.clear();
/*     */   }
/*     */   
/*     */   private void resetStatusListeners() {
/* 239 */     StatusManager sm = getStatusManager();
/* 240 */     for (StatusListener sl : sm.getCopyOfStatusListenerList()) {
/* 241 */       sm.remove(sl);
/*     */     }
/*     */   }
/*     */   
/*     */   public TurboFilterList getTurboFilterList() {
/* 246 */     return this.turboFilterList;
/*     */   }
/*     */   
/*     */   public void addTurboFilter(TurboFilter newFilter) {
/* 250 */     this.turboFilterList.add(newFilter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetTurboFilterList() {
/* 258 */     for (TurboFilter tf : this.turboFilterList) {
/* 259 */       tf.stop();
/*     */     }
/* 261 */     this.turboFilterList.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   final FilterReply getTurboFilterChainDecision_0_3OrMore(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
/* 266 */     if (this.turboFilterList.size() == 0) {
/* 267 */       return FilterReply.NEUTRAL;
/*     */     }
/* 269 */     return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
/*     */   }
/*     */ 
/*     */   
/*     */   final FilterReply getTurboFilterChainDecision_1(Marker marker, Logger logger, Level level, String format, Object param, Throwable t) {
/* 274 */     if (this.turboFilterList.size() == 0) {
/* 275 */       return FilterReply.NEUTRAL;
/*     */     }
/* 277 */     return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param }, t);
/*     */   }
/*     */ 
/*     */   
/*     */   final FilterReply getTurboFilterChainDecision_2(Marker marker, Logger logger, Level level, String format, Object param1, Object param2, Throwable t) {
/* 282 */     if (this.turboFilterList.size() == 0) {
/* 283 */       return FilterReply.NEUTRAL;
/*     */     }
/* 285 */     return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param1, param2 }, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(LoggerContextListener listener) {
/* 290 */     this.loggerContextListenerList.add(listener);
/*     */   }
/*     */   
/*     */   public void removeListener(LoggerContextListener listener) {
/* 294 */     this.loggerContextListenerList.remove(listener);
/*     */   }
/*     */   
/*     */   private void resetListenersExceptResetResistant() {
/* 298 */     List<LoggerContextListener> toRetain = new ArrayList<LoggerContextListener>();
/*     */     
/* 300 */     for (LoggerContextListener lcl : this.loggerContextListenerList) {
/* 301 */       if (lcl.isResetResistant()) {
/* 302 */         toRetain.add(lcl);
/*     */       }
/*     */     } 
/* 305 */     this.loggerContextListenerList.retainAll(toRetain);
/*     */   }
/*     */   
/*     */   private void resetAllListeners() {
/* 309 */     this.loggerContextListenerList.clear();
/*     */   }
/*     */   
/*     */   public List<LoggerContextListener> getCopyOfListenerList() {
/* 313 */     return new ArrayList<LoggerContextListener>(this.loggerContextListenerList);
/*     */   }
/*     */   
/*     */   void fireOnLevelChange(Logger logger, Level level) {
/* 317 */     for (LoggerContextListener listener : this.loggerContextListenerList) {
/* 318 */       listener.onLevelChange(logger, level);
/*     */     }
/*     */   }
/*     */   
/*     */   private void fireOnReset() {
/* 323 */     for (LoggerContextListener listener : this.loggerContextListenerList) {
/* 324 */       listener.onReset(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void fireOnStart() {
/* 329 */     for (LoggerContextListener listener : this.loggerContextListenerList) {
/* 330 */       listener.onStart(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void fireOnStop() {
/* 335 */     for (LoggerContextListener listener : this.loggerContextListenerList) {
/* 336 */       listener.onStop(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 343 */     super.start();
/* 344 */     fireOnStart();
/*     */   }
/*     */   
/*     */   public void stop() {
/* 348 */     reset();
/* 349 */     fireOnStop();
/* 350 */     resetAllListeners();
/* 351 */     super.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 356 */     return getClass().getName() + "[" + getName() + "]";
/*     */   }
/*     */   
/*     */   public int getMaxCallerDataDepth() {
/* 360 */     return this.maxCallerDataDepth;
/*     */   }
/*     */   
/*     */   public void setMaxCallerDataDepth(int maxCallerDataDepth) {
/* 364 */     this.maxCallerDataDepth = maxCallerDataDepth;
/*     */   }
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
/*     */   public List<String> getFrameworkPackages() {
/* 377 */     return this.frameworkPackages;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\LoggerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */