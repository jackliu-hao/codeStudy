package ch.qos.logback.core.joran.spi;

import java.util.HashMap;
import java.util.Map;

public class DefaultNestedComponentRegistry {
   Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap();

   public void add(Class<?> hostClass, String propertyName, Class<?> componentClass) {
      HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
      this.defaultComponentMap.put(hpDouble, componentClass);
   }

   public Class<?> findDefaultComponentType(Class<?> hostClass, String propertyName) {
      for(propertyName = propertyName.toLowerCase(); hostClass != null; hostClass = hostClass.getSuperclass()) {
         Class<?> componentClass = this.oneShotFind(hostClass, propertyName);
         if (componentClass != null) {
            return componentClass;
         }
      }

      return null;
   }

   private Class<?> oneShotFind(Class<?> hostClass, String propertyName) {
      HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
      return (Class)this.defaultComponentMap.get(hpDouble);
   }
}
