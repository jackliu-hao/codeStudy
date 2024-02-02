package org.noear.solon.data.cache;

import java.util.Properties;

public interface CacheFactory {
   CacheService create(Properties props);
}
