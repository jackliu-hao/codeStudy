package ch.qos.logback.core.spi;

import java.util.Collection;
import java.util.Set;

public interface ComponentTracker<C> {
   int DEFAULT_TIMEOUT = 1800000;
   int DEFAULT_MAX_COMPONENTS = Integer.MAX_VALUE;

   int getComponentCount();

   C find(String var1);

   C getOrCreate(String var1, long var2);

   void removeStaleComponents(long var1);

   void endOfLife(String var1);

   Collection<C> allComponents();

   Set<String> allKeys();
}
