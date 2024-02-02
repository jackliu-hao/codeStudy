package ch.qos.logback.classic;

import ch.qos.logback.classic.spi.LoggerComparator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.TurboFilterList;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.ILoggerFactory;
import org.slf4j.Marker;

public class LoggerContext extends ContextBase implements ILoggerFactory, LifeCycle {
   public static final boolean DEFAULT_PACKAGING_DATA = false;
   final Logger root = new Logger("ROOT", (Logger)null, this);
   private int size;
   private int noAppenderWarning = 0;
   private final List<LoggerContextListener> loggerContextListenerList = new ArrayList();
   private Map<String, Logger> loggerCache = new ConcurrentHashMap();
   private LoggerContextVO loggerContextRemoteView = new LoggerContextVO(this);
   private final TurboFilterList turboFilterList = new TurboFilterList();
   private boolean packagingDataEnabled = false;
   private int maxCallerDataDepth = 8;
   int resetCount = 0;
   private List<String> frameworkPackages;

   public LoggerContext() {
      this.root.setLevel(Level.DEBUG);
      this.loggerCache.put("ROOT", this.root);
      this.initEvaluatorMap();
      this.size = 1;
      this.frameworkPackages = new ArrayList();
   }

   void initEvaluatorMap() {
      this.putObject("EVALUATOR_MAP", new HashMap());
   }

   private void updateLoggerContextVO() {
      this.loggerContextRemoteView = new LoggerContextVO(this);
   }

   public void putProperty(String key, String val) {
      super.putProperty(key, val);
      this.updateLoggerContextVO();
   }

   public void setName(String name) {
      super.setName(name);
      this.updateLoggerContextVO();
   }

   public final Logger getLogger(Class<?> clazz) {
      return this.getLogger(clazz.getName());
   }

   public final Logger getLogger(String name) {
      if (name == null) {
         throw new IllegalArgumentException("name argument cannot be null");
      } else if ("ROOT".equalsIgnoreCase(name)) {
         return this.root;
      } else {
         int i = 0;
         Logger logger = this.root;
         Logger childLogger = (Logger)this.loggerCache.get(name);
         if (childLogger != null) {
            return childLogger;
         } else {
            int h;
            do {
               h = LoggerNameUtil.getSeparatorIndexOf(name, i);
               String childName;
               if (h == -1) {
                  childName = name;
               } else {
                  childName = name.substring(0, h);
               }

               i = h + 1;
               synchronized(logger) {
                  childLogger = logger.getChildByName(childName);
                  if (childLogger == null) {
                     childLogger = logger.createChildByName(childName);
                     this.loggerCache.put(childName, childLogger);
                     this.incSize();
                  }
               }

               logger = childLogger;
            } while(h != -1);

            return childLogger;
         }
      }
   }

   private void incSize() {
      ++this.size;
   }

   int size() {
      return this.size;
   }

   public Logger exists(String name) {
      return (Logger)this.loggerCache.get(name);
   }

   final void noAppenderDefinedWarning(Logger logger) {
      if (this.noAppenderWarning++ == 0) {
         this.getStatusManager().add((Status)(new WarnStatus("No appenders present in context [" + this.getName() + "] for logger [" + logger.getName() + "].", logger)));
      }

   }

   public List<Logger> getLoggerList() {
      Collection<Logger> collection = this.loggerCache.values();
      List<Logger> loggerList = new ArrayList(collection);
      Collections.sort(loggerList, new LoggerComparator());
      return loggerList;
   }

   public LoggerContextVO getLoggerContextRemoteView() {
      return this.loggerContextRemoteView;
   }

   public void setPackagingDataEnabled(boolean packagingDataEnabled) {
      this.packagingDataEnabled = packagingDataEnabled;
   }

   public boolean isPackagingDataEnabled() {
      return this.packagingDataEnabled;
   }

   public void reset() {
      ++this.resetCount;
      super.reset();
      this.initEvaluatorMap();
      this.initCollisionMaps();
      this.root.recursiveReset();
      this.resetTurboFilterList();
      this.cancelScheduledTasks();
      this.fireOnReset();
      this.resetListenersExceptResetResistant();
      this.resetStatusListeners();
   }

   private void cancelScheduledTasks() {
      Iterator var1 = this.scheduledFutures.iterator();

      while(var1.hasNext()) {
         ScheduledFuture<?> sf = (ScheduledFuture)var1.next();
         sf.cancel(false);
      }

      this.scheduledFutures.clear();
   }

   private void resetStatusListeners() {
      StatusManager sm = this.getStatusManager();
      Iterator var2 = sm.getCopyOfStatusListenerList().iterator();

      while(var2.hasNext()) {
         StatusListener sl = (StatusListener)var2.next();
         sm.remove(sl);
      }

   }

   public TurboFilterList getTurboFilterList() {
      return this.turboFilterList;
   }

   public void addTurboFilter(TurboFilter newFilter) {
      this.turboFilterList.add(newFilter);
   }

   public void resetTurboFilterList() {
      Iterator var1 = this.turboFilterList.iterator();

      while(var1.hasNext()) {
         TurboFilter tf = (TurboFilter)var1.next();
         tf.stop();
      }

      this.turboFilterList.clear();
   }

   final FilterReply getTurboFilterChainDecision_0_3OrMore(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      return this.turboFilterList.size() == 0 ? FilterReply.NEUTRAL : this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
   }

   final FilterReply getTurboFilterChainDecision_1(Marker marker, Logger logger, Level level, String format, Object param, Throwable t) {
      return this.turboFilterList.size() == 0 ? FilterReply.NEUTRAL : this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[]{param}, t);
   }

   final FilterReply getTurboFilterChainDecision_2(Marker marker, Logger logger, Level level, String format, Object param1, Object param2, Throwable t) {
      return this.turboFilterList.size() == 0 ? FilterReply.NEUTRAL : this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[]{param1, param2}, t);
   }

   public void addListener(LoggerContextListener listener) {
      this.loggerContextListenerList.add(listener);
   }

   public void removeListener(LoggerContextListener listener) {
      this.loggerContextListenerList.remove(listener);
   }

   private void resetListenersExceptResetResistant() {
      List<LoggerContextListener> toRetain = new ArrayList();
      Iterator var2 = this.loggerContextListenerList.iterator();

      while(var2.hasNext()) {
         LoggerContextListener lcl = (LoggerContextListener)var2.next();
         if (lcl.isResetResistant()) {
            toRetain.add(lcl);
         }
      }

      this.loggerContextListenerList.retainAll(toRetain);
   }

   private void resetAllListeners() {
      this.loggerContextListenerList.clear();
   }

   public List<LoggerContextListener> getCopyOfListenerList() {
      return new ArrayList(this.loggerContextListenerList);
   }

   void fireOnLevelChange(Logger logger, Level level) {
      Iterator var3 = this.loggerContextListenerList.iterator();

      while(var3.hasNext()) {
         LoggerContextListener listener = (LoggerContextListener)var3.next();
         listener.onLevelChange(logger, level);
      }

   }

   private void fireOnReset() {
      Iterator var1 = this.loggerContextListenerList.iterator();

      while(var1.hasNext()) {
         LoggerContextListener listener = (LoggerContextListener)var1.next();
         listener.onReset(this);
      }

   }

   private void fireOnStart() {
      Iterator var1 = this.loggerContextListenerList.iterator();

      while(var1.hasNext()) {
         LoggerContextListener listener = (LoggerContextListener)var1.next();
         listener.onStart(this);
      }

   }

   private void fireOnStop() {
      Iterator var1 = this.loggerContextListenerList.iterator();

      while(var1.hasNext()) {
         LoggerContextListener listener = (LoggerContextListener)var1.next();
         listener.onStop(this);
      }

   }

   public void start() {
      super.start();
      this.fireOnStart();
   }

   public void stop() {
      this.reset();
      this.fireOnStop();
      this.resetAllListeners();
      super.stop();
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.getName() + "]";
   }

   public int getMaxCallerDataDepth() {
      return this.maxCallerDataDepth;
   }

   public void setMaxCallerDataDepth(int maxCallerDataDepth) {
      this.maxCallerDataDepth = maxCallerDataDepth;
   }

   public List<String> getFrameworkPackages() {
      return this.frameworkPackages;
   }
}
