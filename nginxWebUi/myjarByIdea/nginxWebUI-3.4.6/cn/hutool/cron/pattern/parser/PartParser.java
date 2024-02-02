package cn.hutool.cron.pattern.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronException;
import cn.hutool.cron.pattern.Part;
import cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher;
import cn.hutool.cron.pattern.matcher.BoolArrayMatcher;
import cn.hutool.cron.pattern.matcher.DayOfMonthMatcher;
import cn.hutool.cron.pattern.matcher.PartMatcher;
import cn.hutool.cron.pattern.matcher.YearValueMatcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartParser {
   private final Part part;

   public static PartParser of(Part part) {
      return new PartParser(part);
   }

   public PartParser(Part part) {
      this.part = part;
   }

   public PartMatcher parse(String value) {
      if (isMatchAllStr(value)) {
         return new AlwaysTrueMatcher();
      } else {
         List<Integer> values = this.parseArray(value);
         if (values.size() == 0) {
            throw new CronException("Invalid part value: [{}]", new Object[]{value});
         } else {
            switch (this.part) {
               case DAY_OF_MONTH:
                  return new DayOfMonthMatcher(values);
               case YEAR:
                  return new YearValueMatcher(values);
               default:
                  return new BoolArrayMatcher(values);
            }
         }
      }
   }

   private List<Integer> parseArray(String value) {
      List<Integer> values = new ArrayList();
      List<String> parts = StrUtil.split(value, ',');
      Iterator var4 = parts.iterator();

      while(var4.hasNext()) {
         String part = (String)var4.next();
         CollUtil.addAllIfNotContains(values, this.parseStep(part));
      }

      return values;
   }

   private List<Integer> parseStep(String value) {
      List<String> parts = StrUtil.split(value, '/');
      int size = parts.size();
      List results;
      if (size == 1) {
         results = this.parseRange(value, -1);
      } else {
         if (size != 2) {
            throw new CronException("Invalid syntax of field: [{}]", new Object[]{value});
         }

         int step = this.parseNumber((String)parts.get(1));
         if (step < 1) {
            throw new CronException("Non positive divisor for field: [{}]", new Object[]{value});
         }

         results = this.parseRange((String)parts.get(0), step);
      }

      return results;
   }

   private List<Integer> parseRange(String value, int step) {
      List<Integer> results = new ArrayList();
      int maxValue;
      int v1;
      if (value.length() <= 2) {
         int minValue = this.part.getMin();
         if (!isMatchAllStr(value)) {
            minValue = Math.max(minValue, this.parseNumber(value));
         } else if (step < 1) {
            step = 1;
         }

         if (step > 0) {
            maxValue = this.part.getMax();
            if (minValue > maxValue) {
               throw new CronException("Invalid value {} > {}", new Object[]{minValue, maxValue});
            }

            for(v1 = minValue; v1 <= maxValue; v1 += step) {
               results.add(v1);
            }
         } else {
            results.add(minValue);
         }

         return results;
      } else {
         List<String> parts = StrUtil.split(value, '-');
         maxValue = parts.size();
         if (maxValue == 1) {
            v1 = this.parseNumber(value);
            if (step > 0) {
               NumberUtil.appendRange(v1, this.part.getMax(), step, results);
            } else {
               results.add(v1);
            }
         } else {
            if (maxValue != 2) {
               throw new CronException("Invalid syntax of field: [{}]", new Object[]{value});
            }

            v1 = this.parseNumber((String)parts.get(0));
            int v2 = this.parseNumber((String)parts.get(1));
            if (step < 1) {
               step = 1;
            }

            if (v1 < v2) {
               NumberUtil.appendRange(v1, v2, step, results);
            } else if (v1 > v2) {
               NumberUtil.appendRange(v1, this.part.getMax(), step, results);
               NumberUtil.appendRange(this.part.getMin(), v2, step, results);
            } else {
               NumberUtil.appendRange(v1, this.part.getMax(), step, results);
            }
         }

         return results;
      }
   }

   private static boolean isMatchAllStr(String value) {
      return 1 == value.length() && ("*".equals(value) || "?".equals(value));
   }

   private int parseNumber(String value) throws CronException {
      int i;
      try {
         i = Integer.parseInt(value);
      } catch (NumberFormatException var4) {
         i = this.parseAlias(value);
      }

      if (i < 0) {
         i += this.part.getMax();
      }

      if (Part.DAY_OF_WEEK.equals(this.part) && Week.SUNDAY.getIso8601Value() == i) {
         i = Week.SUNDAY.ordinal();
      }

      return this.part.checkValue(i);
   }

   private int parseAlias(String name) throws CronException {
      if ("L".equalsIgnoreCase(name)) {
         return this.part.getMax();
      } else {
         switch (this.part) {
            case MONTH:
               return Month.of(name).getValueBaseOne();
            case DAY_OF_WEEK:
               return Week.of(name).ordinal();
            default:
               throw new CronException("Invalid alias value: [{}]", new Object[]{name});
         }
      }
   }
}
