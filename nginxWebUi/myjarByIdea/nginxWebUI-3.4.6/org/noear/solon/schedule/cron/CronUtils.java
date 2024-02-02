package org.noear.solon.schedule.cron;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CronUtils {
   private static Map<String, CronExpressionPlus> cached = new HashMap();

   public static CronExpressionPlus get(String cron) throws ParseException {
      CronExpressionPlus expr = (CronExpressionPlus)cached.get(cron);
      if (expr == null) {
         synchronized(cron.intern()) {
            expr = (CronExpressionPlus)cached.get(cron);
            if (expr == null) {
               int tzIdx = cron.indexOf("+");
               if (tzIdx < 0) {
                  tzIdx = cron.indexOf("-");
               }

               if (tzIdx > 0) {
                  String tz = cron.substring(tzIdx);
                  ZoneOffset tz2 = ZoneOffset.of(tz);
                  cron = cron.substring(0, tzIdx - 1);
                  expr = new CronExpressionPlus(cron);
                  expr.setTimeZone(TimeZone.getTimeZone(tz2));
               } else {
                  expr = new CronExpressionPlus(cron);
               }

               cached.put(cron, expr);
            }
         }
      }

      return expr;
   }

   public static Date getNextTime(String cron, Date baseTime) throws ParseException {
      return get(cron).getNextValidTimeAfter(baseTime);
   }

   public static boolean isValid(String cron) {
      try {
         return get(cron) != null;
      } catch (ParseException var2) {
         return false;
      }
   }
}
