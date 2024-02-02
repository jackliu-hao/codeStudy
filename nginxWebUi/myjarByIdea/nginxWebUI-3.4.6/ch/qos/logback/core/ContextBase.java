package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ContextBase implements Context, LifeCycle {
   private long birthTime = System.currentTimeMillis();
   private String name;
   private StatusManager sm = new BasicStatusManager();
   Map<String, String> propertyMap = new HashMap();
   Map<String, Object> objectMap = new HashMap();
   LogbackLock configurationLock = new LogbackLock();
   private ScheduledExecutorService scheduledExecutorService;
   protected List<ScheduledFuture<?>> scheduledFutures = new ArrayList(1);
   private LifeCycleManager lifeCycleManager;
   private boolean started;

   public ContextBase() {
      this.initCollisionMaps();
   }

   public StatusManager getStatusManager() {
      return this.sm;
   }

   public void setStatusManager(StatusManager statusManager) {
      if (statusManager == null) {
         throw new IllegalArgumentException("null StatusManager not allowed");
      } else {
         this.sm = statusManager;
      }
   }

   public Map<String, String> getCopyOfPropertyMap() {
      return new HashMap(this.propertyMap);
   }

   public void putProperty(String key, String val) {
      if ("HOSTNAME".equalsIgnoreCase(key)) {
         this.putHostnameProperty(val);
      } else {
         this.propertyMap.put(key, val);
      }

   }

   protected void initCollisionMaps() {
      this.putObject("FA_FILENAME_COLLISION_MAP", new HashMap());
      this.putObject("RFA_FILENAME_PATTERN_COLLISION_MAP", new HashMap());
   }

   public String getProperty(String key) {
      if ("CONTEXT_NAME".equals(key)) {
         return this.getName();
      } else {
         return "HOSTNAME".equalsIgnoreCase(key) ? this.lazyGetHostname() : (String)this.propertyMap.get(key);
      }
   }

   private String lazyGetHostname() {
      String hostname = (String)this.propertyMap.get("HOSTNAME");
      if (hostname == null) {
         hostname = (new ContextUtil(this)).safelyGetLocalHostName();
         this.putHostnameProperty(hostname);
      }

      return hostname;
   }

   private void putHostnameProperty(String hostname) {
      String existingHostname = (String)this.propertyMap.get("HOSTNAME");
      if (existingHostname == null) {
         this.propertyMap.put("HOSTNAME", hostname);
      }

   }

   public Object getObject(String key) {
      return this.objectMap.get(key);
   }

   public void putObject(String key, Object value) {
      this.objectMap.put(key, value);
   }

   public void removeObject(String key) {
      this.objectMap.remove(key);
   }

   public String getName() {
      return this.name;
   }

   public void start() {
      this.started = true;
   }

   public void stop() {
      this.stopExecutorService();
      this.started = false;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void reset() {
      this.removeShutdownHook();
      this.getLifeCycleManager().reset();
      this.propertyMap.clear();
      this.objectMap.clear();
   }

   public void setName(String name) throws IllegalStateException {
      if (name == null || !name.equals(this.name)) {
         if (this.name != null && !"default".equals(this.name)) {
            throw new IllegalStateException("Context has been already given a name");
         } else {
            this.name = name;
         }
      }
   }

   public long getBirthTime() {
      return this.birthTime;
   }

   public Object getConfigurationLock() {
      return this.configurationLock;
   }

   public synchronized ExecutorService getExecutorService() {
      return this.getScheduledExecutorService();
   }

   public synchronized ScheduledExecutorService getScheduledExecutorService() {
      if (this.scheduledExecutorService == null) {
         this.scheduledExecutorService = ExecutorServiceUtil.newScheduledExecutorService();
      }

      return this.scheduledExecutorService;
   }

   private synchronized void stopExecutorService() {
      if (this.scheduledExecutorService != null) {
         ExecutorServiceUtil.shutdown(this.scheduledExecutorService);
         this.scheduledExecutorService = null;
      }

   }

   private void removeShutdownHook() {
      Thread hook = (Thread)this.getObject("SHUTDOWN_HOOK");
      if (hook != null) {
         this.removeObject("SHUTDOWN_HOOK");

         try {
            Runtime.getRuntime().removeShutdownHook(hook);
         } catch (IllegalStateException var3) {
         }
      }

   }

   public void register(LifeCycle component) {
      this.getLifeCycleManager().register(component);
   }

   synchronized LifeCycleManager getLifeCycleManager() {
      if (this.lifeCycleManager == null) {
         this.lifeCycleManager = new LifeCycleManager();
      }

      return this.lifeCycleManager;
   }

   public String toString() {
      return this.name;
   }

   public void addScheduledFuture(ScheduledFuture<?> scheduledFuture) {
      this.scheduledFutures.add(scheduledFuture);
   }

   public List<ScheduledFuture<?>> getScheduledFutures() {
      return new ArrayList(this.scheduledFutures);
   }
}
