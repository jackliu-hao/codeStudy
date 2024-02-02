/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import ch.qos.logback.core.spi.LogbackLock;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.util.ContextUtil;
/*     */ import ch.qos.logback.core.util.ExecutorServiceUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
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
/*     */ public class ContextBase
/*     */   implements Context, LifeCycle
/*     */ {
/*  38 */   private long birthTime = System.currentTimeMillis();
/*     */   
/*     */   private String name;
/*  41 */   private StatusManager sm = new BasicStatusManager();
/*     */ 
/*     */ 
/*     */   
/*  45 */   Map<String, String> propertyMap = new HashMap<String, String>();
/*  46 */   Map<String, Object> objectMap = new HashMap<String, Object>();
/*     */   
/*  48 */   LogbackLock configurationLock = new LogbackLock();
/*     */   
/*     */   private ScheduledExecutorService scheduledExecutorService;
/*  51 */   protected List<ScheduledFuture<?>> scheduledFutures = new ArrayList<ScheduledFuture<?>>(1);
/*     */   private LifeCycleManager lifeCycleManager;
/*     */   private boolean started;
/*     */   
/*     */   public ContextBase() {
/*  56 */     initCollisionMaps();
/*     */   }
/*     */   
/*     */   public StatusManager getStatusManager() {
/*  60 */     return this.sm;
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
/*     */ 
/*     */   
/*     */   public void setStatusManager(StatusManager statusManager) {
/*  75 */     if (statusManager == null) {
/*  76 */       throw new IllegalArgumentException("null StatusManager not allowed");
/*     */     }
/*  78 */     this.sm = statusManager;
/*     */   }
/*     */   
/*     */   public Map<String, String> getCopyOfPropertyMap() {
/*  82 */     return new HashMap<String, String>(this.propertyMap);
/*     */   }
/*     */   
/*     */   public void putProperty(String key, String val) {
/*  86 */     if ("HOSTNAME".equalsIgnoreCase(key)) {
/*  87 */       putHostnameProperty(val);
/*     */     } else {
/*  89 */       this.propertyMap.put(key, val);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void initCollisionMaps() {
/*  94 */     putObject("FA_FILENAME_COLLISION_MAP", new HashMap<Object, Object>());
/*  95 */     putObject("RFA_FILENAME_PATTERN_COLLISION_MAP", new HashMap<Object, Object>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProperty(String key) {
/* 106 */     if ("CONTEXT_NAME".equals(key))
/* 107 */       return getName(); 
/* 108 */     if ("HOSTNAME".equalsIgnoreCase(key)) {
/* 109 */       return lazyGetHostname();
/*     */     }
/*     */     
/* 112 */     return this.propertyMap.get(key);
/*     */   }
/*     */   
/*     */   private String lazyGetHostname() {
/* 116 */     String hostname = this.propertyMap.get("HOSTNAME");
/* 117 */     if (hostname == null) {
/* 118 */       hostname = (new ContextUtil(this)).safelyGetLocalHostName();
/* 119 */       putHostnameProperty(hostname);
/*     */     } 
/* 121 */     return hostname;
/*     */   }
/*     */   
/*     */   private void putHostnameProperty(String hostname) {
/* 125 */     String existingHostname = this.propertyMap.get("HOSTNAME");
/* 126 */     if (existingHostname == null) {
/* 127 */       this.propertyMap.put("HOSTNAME", hostname);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getObject(String key) {
/* 134 */     return this.objectMap.get(key);
/*     */   }
/*     */   
/*     */   public void putObject(String key, Object value) {
/* 138 */     this.objectMap.put(key, value);
/*     */   }
/*     */   
/*     */   public void removeObject(String key) {
/* 142 */     this.objectMap.remove(key);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 146 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 153 */     this.started = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 159 */     stopExecutorService();
/*     */     
/* 161 */     this.started = false;
/*     */   }
/*     */   
/*     */   public boolean isStarted() {
/* 165 */     return this.started;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 174 */     removeShutdownHook();
/* 175 */     getLifeCycleManager().reset();
/* 176 */     this.propertyMap.clear();
/* 177 */     this.objectMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) throws IllegalStateException {
/* 188 */     if (name != null && name.equals(this.name)) {
/*     */       return;
/*     */     }
/* 191 */     if (this.name == null || "default".equals(this.name)) {
/* 192 */       this.name = name;
/*     */     } else {
/* 194 */       throw new IllegalStateException("Context has been already given a name");
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getBirthTime() {
/* 199 */     return this.birthTime;
/*     */   }
/*     */   
/*     */   public Object getConfigurationLock() {
/* 203 */     return this.configurationLock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized ExecutorService getExecutorService() {
/* 211 */     return getScheduledExecutorService();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized ScheduledExecutorService getScheduledExecutorService() {
/* 216 */     if (this.scheduledExecutorService == null) {
/* 217 */       this.scheduledExecutorService = ExecutorServiceUtil.newScheduledExecutorService();
/*     */     }
/* 219 */     return this.scheduledExecutorService;
/*     */   }
/*     */   
/*     */   private synchronized void stopExecutorService() {
/* 223 */     if (this.scheduledExecutorService != null) {
/* 224 */       ExecutorServiceUtil.shutdown(this.scheduledExecutorService);
/* 225 */       this.scheduledExecutorService = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void removeShutdownHook() {
/* 230 */     Thread hook = (Thread)getObject("SHUTDOWN_HOOK");
/* 231 */     if (hook != null) {
/* 232 */       removeObject("SHUTDOWN_HOOK");
/*     */       try {
/* 234 */         Runtime.getRuntime().removeShutdownHook(hook);
/* 235 */       } catch (IllegalStateException illegalStateException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void register(LifeCycle component) {
/* 243 */     getLifeCycleManager().register(component);
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
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized LifeCycleManager getLifeCycleManager() {
/* 259 */     if (this.lifeCycleManager == null) {
/* 260 */       this.lifeCycleManager = new LifeCycleManager();
/*     */     }
/* 262 */     return this.lifeCycleManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 267 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addScheduledFuture(ScheduledFuture<?> scheduledFuture) {
/* 272 */     this.scheduledFutures.add(scheduledFuture);
/*     */   }
/*     */   
/*     */   public List<ScheduledFuture<?>> getScheduledFutures() {
/* 276 */     return new ArrayList<ScheduledFuture<?>>(this.scheduledFutures);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\ContextBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */