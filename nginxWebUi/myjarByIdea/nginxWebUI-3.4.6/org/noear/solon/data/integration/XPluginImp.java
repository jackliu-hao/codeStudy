package org.noear.solon.data.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.around.CacheInterceptor;
import org.noear.solon.data.around.CachePutInterceptor;
import org.noear.solon.data.around.CacheRemoveInterceptor;
import org.noear.solon.data.around.TranInterceptor;
import org.noear.solon.data.cache.CacheLib;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceEventListener;
import org.noear.solon.data.cache.LocalCacheFactoryImpl;
import org.noear.solon.data.cache.LocalCacheService;
import org.noear.solon.data.tran.TranExecutor;
import org.noear.solon.data.tran.TranExecutorImp;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      CacheLib.cacheFactoryAdd("local", new LocalCacheFactoryImpl());
      if (Solon.app().enableTransaction()) {
         context.wrapAndPut(TranExecutor.class, TranExecutorImp.global);
         context.beanAroundAdd(Tran.class, new TranInterceptor(), 120);
      }

      if (Solon.app().enableCaching()) {
         CacheLib.cacheServiceAddIfAbsent("", LocalCacheService.instance);
         Solon.app().onEvent(BeanWrap.class, new CacheServiceEventListener());
         context.beanOnloaded((ctx) -> {
            if (!ctx.hasWrap(CacheService.class)) {
               ctx.wrapAndPut(CacheService.class, LocalCacheService.instance);
            }

         });
         context.beanAroundAdd(CachePut.class, new CachePutInterceptor(), 110);
         context.beanAroundAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
         context.beanAroundAdd(Cache.class, new CacheInterceptor(), 111);
      }

   }
}
