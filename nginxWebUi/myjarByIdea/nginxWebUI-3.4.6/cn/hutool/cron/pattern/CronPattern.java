package cn.hutool.cron.pattern;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.CalendarUtil;
import cn.hutool.cron.pattern.matcher.PatternMatcher;
import cn.hutool.cron.pattern.parser.PatternParser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class CronPattern {
   private final String pattern;
   private final List<PatternMatcher> matchers;

   public static CronPattern of(String pattern) {
      return new CronPattern(pattern);
   }

   public CronPattern(String pattern) {
      this.pattern = pattern;
      this.matchers = PatternParser.parse(pattern);
   }

   public boolean match(long millis, boolean isMatchSecond) {
      return this.match(TimeZone.getDefault(), millis, isMatchSecond);
   }

   public boolean match(TimeZone timezone, long millis, boolean isMatchSecond) {
      GregorianCalendar calendar = new GregorianCalendar(timezone);
      calendar.setTimeInMillis(millis);
      return this.match((Calendar)calendar, isMatchSecond);
   }

   public boolean match(Calendar calendar, boolean isMatchSecond) {
      return this.match(PatternUtil.getFields(calendar, isMatchSecond));
   }

   public boolean match(LocalDateTime dateTime, boolean isMatchSecond) {
      return this.match(PatternUtil.getFields(dateTime, isMatchSecond));
   }

   public Calendar nextMatchAfter(Calendar calendar) {
      Calendar next = this.nextMatchAfter(PatternUtil.getFields(calendar, true), calendar.getTimeZone());
      if (!this.match(next, true)) {
         next.set(5, next.get(5) + 1);
         next = CalendarUtil.beginOfDay(next);
         return this.nextMatchAfter(next);
      } else {
         return next;
      }
   }

   public String toString() {
      return this.pattern;
   }

   private boolean match(int[] fields) {
      Iterator var2 = this.matchers.iterator();

      PatternMatcher matcher;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         matcher = (PatternMatcher)var2.next();
      } while(!matcher.match(fields));

      return true;
   }

   private Calendar nextMatchAfter(int[] values, TimeZone zone) {
      List<Calendar> nextMatches = new ArrayList(this.matchers.size());
      Iterator var4 = this.matchers.iterator();

      while(var4.hasNext()) {
         PatternMatcher matcher = (PatternMatcher)var4.next();
         nextMatches.add(matcher.nextMatchAfter(values, zone));
      }

      return (Calendar)CollUtil.min(nextMatches);
   }
}
