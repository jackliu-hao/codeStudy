package org.noear.solon.data.around;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.cache.CacheExecutorImp;

public class CacheRemoveInterceptor implements Interceptor {
   public Object doIntercept(Invocation inv) throws Throwable {
      Object tmp = inv.invoke();
      CacheRemove anno = (CacheRemove)inv.method().getAnnotation(CacheRemove.class);
      CacheExecutorImp.global.cacheRemove(anno, inv, tmp);
      return tmp;
   }
}
