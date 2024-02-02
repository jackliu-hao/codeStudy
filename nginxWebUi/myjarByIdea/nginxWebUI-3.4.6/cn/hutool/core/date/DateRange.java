package cn.hutool.core.date;

import cn.hutool.core.lang.Range;
import java.util.Date;

public class DateRange extends Range<DateTime> {
   private static final long serialVersionUID = 1L;

   public DateRange(Date start, Date end, DateField unit) {
      this(start, end, unit, 1);
   }

   public DateRange(Date start, Date end, DateField unit, int step) {
      this(start, end, unit, step, true, true);
   }

   public DateRange(Date start, Date end, DateField unit, int step, boolean isIncludeStart, boolean isIncludeEnd) {
      super(DateUtil.date(start), DateUtil.date(end), (current, end1, index) -> {
         DateTime dt = DateUtil.date(start).offsetNew(unit, (index + 1) * step);
         return dt.isAfter(end1) ? null : dt;
      }, isIncludeStart, isIncludeEnd);
   }
}
