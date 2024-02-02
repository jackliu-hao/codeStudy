package org.noear.solon.data.cache;

import java.util.Properties;
import java.util.function.Supplier;

public class CacheServiceSupplier implements Supplier<CacheService> {
   private CacheService real;
   private String driverType;

   public CacheServiceSupplier(Properties props) {
      this.driverType = props.getProperty("driverType");
      CacheFactory factory = CacheLib.cacheFactoryGet(this.driverType);
      if (factory != null) {
         this.real = factory.create(props);
      } else {
         throw new IllegalArgumentException("There is no supported driverType");
      }
   }

   public CacheService get() {
      return this.real;
   }
}
