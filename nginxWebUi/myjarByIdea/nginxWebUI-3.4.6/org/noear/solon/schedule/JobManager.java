package org.noear.solon.schedule;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import org.noear.solon.Utils;
import org.noear.solon.schedule.cron.CronExpressionPlus;
import org.noear.solon.schedule.cron.CronUtils;

public class JobManager {
   private static Map<String, JobEntity> jobEntityMap = new HashMap();
   private static boolean isStarted = false;

   public static void add(String name, String cron, boolean concurrent, Runnable runnable) throws ParseException {
      CronExpressionPlus cronX = CronUtils.get(cron);
      addDo(name, new JobEntity(name, cronX, concurrent, runnable));
   }

   public static void add(String name, String cron, String zone, boolean concurrent, Runnable runnable) throws ParseException {
      CronExpressionPlus cronX = CronUtils.get(cron);
      if (Utils.isNotEmpty(zone)) {
         cronX.setTimeZone(TimeZone.getTimeZone(zone));
      }

      addDo(name, new JobEntity(name, cronX, concurrent, runnable));
   }

   public static void add(String name, long fixedRate, boolean concurrent, Runnable runnable) {
      addDo(name, new JobEntity(name, fixedRate, 0L, concurrent, runnable));
   }

   public static void add(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
      addDo(name, new JobEntity(name, fixedRate, fixedDelay, concurrent, runnable));
   }

   private static void addDo(String name, JobEntity jobEntity) {
      jobEntityMap.putIfAbsent(name, jobEntity);
      if (isStarted) {
         jobEntity.start();
      }

   }

   public static boolean contains(String name) {
      return jobEntityMap.containsKey(name);
   }

   public static void remove(String name) {
      JobEntity jobEntity = (JobEntity)jobEntityMap.get(name);
      if (jobEntity != null) {
         jobEntity.cancel();
         jobEntityMap.remove(name);
      }

   }

   public static Runnable getRunnable(String name) {
      JobEntity jobEntity = (JobEntity)jobEntityMap.get(name);
      return jobEntity != null ? jobEntity.runnable : null;
   }

   public static void start() {
      Iterator var0 = jobEntityMap.values().iterator();

      while(var0.hasNext()) {
         JobEntity job = (JobEntity)var0.next();
         job.start();
      }

      isStarted = true;
   }

   public static void stop() {
      Iterator var0 = jobEntityMap.values().iterator();

      while(var0.hasNext()) {
         JobEntity job = (JobEntity)var0.next();
         job.cancel();
      }

      isStarted = false;
   }
}
