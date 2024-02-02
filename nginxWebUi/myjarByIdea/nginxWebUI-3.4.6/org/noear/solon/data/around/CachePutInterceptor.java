package org.noear.solon.data.around;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.cache.CacheExecutorImp;

public class CachePutInterceptor implements Interceptor {
   public Object doIntercept(Invocation inv) throws Throwable {
      Object tmp = inv.invoke();
      CachePut anno = (CachePut)inv.method().getAnnotation(CachePut.class);
      CacheExecutorImp.global.cachePut(anno, inv, tmp);
      return tmp;
   }
}
