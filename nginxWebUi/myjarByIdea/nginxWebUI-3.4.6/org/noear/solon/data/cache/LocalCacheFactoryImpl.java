package org.noear.solon.data.cache;

import java.util.Properties;

public class LocalCacheFactoryImpl implements CacheFactory {
   public CacheService create(Properties props) {
      return new LocalCacheService(props);
   }
}
