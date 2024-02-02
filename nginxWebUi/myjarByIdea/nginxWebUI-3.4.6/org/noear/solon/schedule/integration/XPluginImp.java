package org.noear.solon.schedule.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.schedule.JobManager;
import org.noear.solon.schedule.MethodRunnable;
import org.noear.solon.schedule.annotation.EnableScheduling;
import org.noear.solon.schedule.annotation.Scheduled;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      if (Solon.app().source().getAnnotation(EnableScheduling.class) != null) {
         context.beanBuilderAdd(Scheduled.class, (clz, bw, anno) -> {
            if (Runnable.class.isAssignableFrom(clz)) {
               String name = Utils.annoAlias(anno.name(), clz.getSimpleName());
               if (anno.fixedRate() > 0L) {
                  JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), (Runnable)bw.raw());
               } else {
                  JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), (Runnable)bw.raw());
               }
            }

         });
         context.beanExtractorAdd(Scheduled.class, (bw, method, anno) -> {
            MethodRunnable runnable = new MethodRunnable(bw.raw(), method);
            String name = Utils.annoAlias(anno.name(), method.getName());
            if (anno.fixedRate() > 0L) {
               JobManager.add(name, anno.fixedRate(), anno.fixedDelay(), anno.concurrent(), runnable);
            } else {
               JobManager.add(name, anno.cron(), anno.zone(), anno.concurrent(), runnable);
            }

         });
         Solon.app().onEvent(AppLoadEndEvent.class, (e) -> {
            JobManager.start();
         });
      }
   }

   public void stop() throws Throwable {
      JobManager.stop();
   }
}
