package org.noear.solon.data.around;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.cache.CacheExecutorImp;

public class CacheInterceptor implements Interceptor {
   public Object doIntercept(Invocation inv) throws Throwable {
      Cache anno = (Cache)inv.method().getAnnotation(Cache.class);
      return CacheExecutorImp.global.cache(anno, inv, () -> {
         return inv.invoke();
      });
   }
}
