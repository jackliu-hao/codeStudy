package cn.hutool.cron.pattern;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CronPatternUtil {
   public static Date nextDateAfter(CronPattern pattern, Date start, boolean isMatchSecond) {
      List<Date> matchedDates = matchedDates((CronPattern)pattern, start.getTime(), DateUtil.endOfYear(start).getTime(), 1, isMatchSecond);
      return CollUtil.isNotEmpty((Collection)matchedDates) ? (Date)matchedDates.get(0) : null;
   }

   public static List<Date> matchedDates(String patternStr, Date start, int count, boolean isMatchSecond) {
      return matchedDates(patternStr, start, DateUtil.endOfYear(start), count, isMatchSecond);
   }

   public static List<Date> matchedDates(String patternStr, Date start, Date end, int count, boolean isMatchSecond) {
      return matchedDates(patternStr, start.getTime(), end.getTime(), count, isMatchSecond);
   }

   public static List<Date> matchedDates(String patternStr, long start, long end, int count, boolean isMatchSecond) {
      return matchedDates(new CronPattern(patternStr), start, end, count, isMatchSecond);
   }

   public static List<Date> matchedDates(CronPattern pattern, long start, long end, int count, boolean isMatchSecond) {
      Assert.isTrue(start < end, "Start date is later than end !");
      List<Date> result = new ArrayList(count);
      long step = isMatchSecond ? DateUnit.SECOND.getMillis() : DateUnit.MINUTE.getMillis();

      for(long i = start; i < end; i += step) {
         if (pattern.match(i, isMatchSecond)) {
            result.add(DateUtil.date(i));
            if (result.size() >= count) {
               break;
            }
         }
      }

      return result;
   }
}
