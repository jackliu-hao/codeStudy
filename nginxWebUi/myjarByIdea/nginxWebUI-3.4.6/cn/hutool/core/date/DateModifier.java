package cn.hutool.core.date;

import cn.hutool.core.util.ArrayUtil;
import java.util.Calendar;

public class DateModifier {
   private static final int[] IGNORE_FIELDS = new int[]{11, 9, 8, 6, 4, 3};

   public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType) {
      return modify(calendar, dateField, modifyType, false);
   }

   public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType, boolean truncateMillisecond) {
      int i;
      if (9 == dateField) {
         boolean isAM = DateUtil.isAM(calendar);
         switch (modifyType) {
            case TRUNCATE:
               calendar.set(11, isAM ? 0 : 12);
               break;
            case CEILING:
               calendar.set(11, isAM ? 11 : 23);
               break;
            case ROUND:
               i = isAM ? 0 : 12;
               int max = isAM ? 11 : 23;
               int href = (max - i) / 2 + 1;
               int value = calendar.get(11);
               calendar.set(11, value < href ? i : max);
         }

         return modify(calendar, dateField + 1, modifyType);
      } else {
         int endField = truncateMillisecond ? 13 : 14;

         for(i = dateField + 1; i <= endField; ++i) {
            if (!ArrayUtil.contains(IGNORE_FIELDS, i)) {
               if (4 != dateField && 3 != dateField) {
                  if (7 == i) {
                     continue;
                  }
               } else if (5 == i) {
                  continue;
               }

               modifyField(calendar, i, modifyType);
            }
         }

         if (truncateMillisecond) {
            calendar.set(14, 0);
         }

         return calendar;
      }
   }

   private static void modifyField(Calendar calendar, int field, ModifyType modifyType) {
      if (10 == field) {
         field = 11;
      }

      switch (modifyType) {
         case TRUNCATE:
            calendar.set(field, DateUtil.getBeginValue(calendar, field));
            break;
         case CEILING:
            calendar.set(field, DateUtil.getEndValue(calendar, field));
            break;
         case ROUND:
            int min = DateUtil.getBeginValue(calendar, field);
            int max = DateUtil.getEndValue(calendar, field);
            int href;
            if (7 == field) {
               href = (min + 3) % 7;
            } else {
               href = (max - min) / 2 + 1;
            }

            int value = calendar.get(field);
            calendar.set(field, value < href ? min : max);
      }

   }

   public static enum ModifyType {
      TRUNCATE,
      ROUND,
      CEILING;
   }
}
