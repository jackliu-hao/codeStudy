package cn.hutool.core.date;

import cn.hutool.core.lang.Assert;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DateBetween implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Date begin;
   private final Date end;

   public static DateBetween create(Date begin, Date end) {
      return new DateBetween(begin, end);
   }

   public static DateBetween create(Date begin, Date end, boolean isAbs) {
      return new DateBetween(begin, end, isAbs);
   }

   public DateBetween(Date begin, Date end) {
      this(begin, end, true);
   }

   public DateBetween(Date begin, Date end, boolean isAbs) {
      Assert.notNull(begin, "Begin date is null !");
      Assert.notNull(end, "End date is null !");
      if (isAbs && begin.after(end)) {
         this.begin = end;
         this.end = begin;
      } else {
         this.begin = begin;
         this.end = end;
      }

   }

   public long between(DateUnit unit) {
      long diff = this.end.getTime() - this.begin.getTime();
      return diff / unit.getMillis();
   }

   public long betweenMonth(boolean isReset) {
      Calendar beginCal = DateUtil.calendar(this.begin);
      Calendar endCal = DateUtil.calendar(this.end);
      int betweenYear = endCal.get(1) - beginCal.get(1);
      int betweenMonthOfYear = endCal.get(2) - beginCal.get(2);
      int result = betweenYear * 12 + betweenMonthOfYear;
      if (!isReset) {
         endCal.set(1, beginCal.get(1));
         endCal.set(2, beginCal.get(2));
         long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
         if (between < 0L) {
            return (long)(result - 1);
         }
      }

      return (long)result;
   }

   public long betweenYear(boolean isReset) {
      Calendar beginCal = DateUtil.calendar(this.begin);
      Calendar endCal = DateUtil.calendar(this.end);
      int result = endCal.get(1) - beginCal.get(1);
      if (!isReset) {
         if (1 == beginCal.get(2) && 1 == endCal.get(2) && beginCal.get(5) == beginCal.getActualMaximum(5) && endCal.get(5) == endCal.getActualMaximum(5)) {
            beginCal.set(5, 1);
            endCal.set(5, 1);
         }

         endCal.set(1, beginCal.get(1));
         long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
         if (between < 0L) {
            return (long)(result - 1);
         }
      }

      return (long)result;
   }

   public String toString(DateUnit unit, BetweenFormatter.Level level) {
      return DateUtil.formatBetween(this.between(unit), level);
   }

   public String toString(BetweenFormatter.Level level) {
      return this.toString(DateUnit.MS, level);
   }

   public String toString() {
      return this.toString(BetweenFormatter.Level.MILLISECOND);
   }
}
