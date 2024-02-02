package org.h2.api;

public enum IntervalQualifier {
   YEAR,
   MONTH,
   DAY,
   HOUR,
   MINUTE,
   SECOND,
   YEAR_TO_MONTH,
   DAY_TO_HOUR,
   DAY_TO_MINUTE,
   DAY_TO_SECOND,
   HOUR_TO_MINUTE,
   HOUR_TO_SECOND,
   MINUTE_TO_SECOND;

   private final String string = this.name().replace('_', ' ').intern();

   public static IntervalQualifier valueOf(int var0) {
      switch (var0) {
         case 0:
            return YEAR;
         case 1:
            return MONTH;
         case 2:
            return DAY;
         case 3:
            return HOUR;
         case 4:
            return MINUTE;
         case 5:
            return SECOND;
         case 6:
            return YEAR_TO_MONTH;
         case 7:
            return DAY_TO_HOUR;
         case 8:
            return DAY_TO_MINUTE;
         case 9:
            return DAY_TO_SECOND;
         case 10:
            return HOUR_TO_MINUTE;
         case 11:
            return HOUR_TO_SECOND;
         case 12:
            return MINUTE_TO_SECOND;
         default:
            throw new IllegalArgumentException();
      }
   }

   public boolean isYearMonth() {
      return this == YEAR || this == MONTH || this == YEAR_TO_MONTH;
   }

   public boolean isDayTime() {
      return !this.isYearMonth();
   }

   public boolean hasYears() {
      return this == YEAR || this == YEAR_TO_MONTH;
   }

   public boolean hasMonths() {
      return this == MONTH || this == YEAR_TO_MONTH;
   }

   public boolean hasDays() {
      switch (this) {
         case DAY:
         case DAY_TO_HOUR:
         case DAY_TO_MINUTE:
         case DAY_TO_SECOND:
            return true;
         default:
            return false;
      }
   }

   public boolean hasHours() {
      switch (this) {
         case DAY_TO_HOUR:
         case DAY_TO_MINUTE:
         case DAY_TO_SECOND:
         case HOUR:
         case HOUR_TO_MINUTE:
         case HOUR_TO_SECOND:
            return true;
         default:
            return false;
      }
   }

   public boolean hasMinutes() {
      switch (this) {
         case DAY_TO_MINUTE:
         case DAY_TO_SECOND:
         case HOUR_TO_MINUTE:
         case HOUR_TO_SECOND:
         case MINUTE:
         case MINUTE_TO_SECOND:
            return true;
         case HOUR:
         default:
            return false;
      }
   }

   public boolean hasSeconds() {
      switch (this) {
         case DAY_TO_SECOND:
         case HOUR_TO_SECOND:
         case MINUTE_TO_SECOND:
         case SECOND:
            return true;
         case HOUR:
         case HOUR_TO_MINUTE:
         case MINUTE:
         default:
            return false;
      }
   }

   public boolean hasMultipleFields() {
      return this.ordinal() > 5;
   }

   public String toString() {
      return this.string;
   }

   public String getTypeName(int var1, int var2) {
      return this.getTypeName(new StringBuilder(), var1, var2, false).toString();
   }

   public StringBuilder getTypeName(StringBuilder var1, int var2, int var3, boolean var4) {
      if (!var4) {
         var1.append("INTERVAL ");
      }

      switch (this) {
         case DAY:
         case HOUR:
         case MINUTE:
         case YEAR:
         case MONTH:
            var1.append(this.string);
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }
            break;
         case DAY_TO_HOUR:
            var1.append("DAY");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO HOUR");
            break;
         case DAY_TO_MINUTE:
            var1.append("DAY");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO MINUTE");
            break;
         case DAY_TO_SECOND:
            var1.append("DAY");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO SECOND");
            if (var3 >= 0) {
               var1.append('(').append(var3).append(')');
            }
            break;
         case HOUR_TO_MINUTE:
            var1.append("HOUR");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO MINUTE");
            break;
         case HOUR_TO_SECOND:
            var1.append("HOUR");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO SECOND");
            if (var3 >= 0) {
               var1.append('(').append(var3).append(')');
            }
            break;
         case MINUTE_TO_SECOND:
            var1.append("MINUTE");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO SECOND");
            if (var3 >= 0) {
               var1.append('(').append(var3).append(')');
            }
            break;
         case SECOND:
            var1.append(this.string);
            if (var2 > 0 || var3 >= 0) {
               var1.append('(').append(var2 > 0 ? var2 : 2);
               if (var3 >= 0) {
                  var1.append(", ").append(var3);
               }

               var1.append(')');
            }
            break;
         case YEAR_TO_MONTH:
            var1.append("YEAR");
            if (var2 > 0) {
               var1.append('(').append(var2).append(')');
            }

            var1.append(" TO MONTH");
      }

      return var1;
   }
}
