package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.status.StatusManager;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public interface Context extends PropertyContainer {
   StatusManager getStatusManager();

   Object getObject(String var1);

   void putObject(String var1, Object var2);

   String getProperty(String var1);

   void putProperty(String var1, String var2);

   Map<String, String> getCopyOfPropertyMap();

   String getName();

   void setName(String var1);

   long getBirthTime();

   Object getConfigurationLock();

   ScheduledExecutorService getScheduledExecutorService();

   /** @deprecated */
   ExecutorService getExecutorService();

   void register(LifeCycle var1);

   void addScheduledFuture(ScheduledFuture<?> var1);
}
