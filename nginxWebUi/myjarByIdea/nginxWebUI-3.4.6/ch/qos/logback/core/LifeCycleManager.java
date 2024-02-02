package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LifeCycleManager {
   private final Set<LifeCycle> components = new HashSet();

   public void register(LifeCycle component) {
      this.components.add(component);
   }

   public void reset() {
      Iterator var1 = this.components.iterator();

      while(var1.hasNext()) {
         LifeCycle component = (LifeCycle)var1.next();
         if (component.isStarted()) {
            component.stop();
         }
      }

      this.components.clear();
   }
}
