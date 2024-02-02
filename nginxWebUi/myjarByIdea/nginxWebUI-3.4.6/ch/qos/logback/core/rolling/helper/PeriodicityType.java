package ch.qos.logback.core.rolling.helper;

public enum PeriodicityType {
   ERRONEOUS,
   TOP_OF_MILLISECOND,
   TOP_OF_SECOND,
   TOP_OF_MINUTE,
   TOP_OF_HOUR,
   HALF_DAY,
   TOP_OF_DAY,
   TOP_OF_WEEK,
   TOP_OF_MONTH;

   static PeriodicityType[] VALID_ORDERED_LIST = new PeriodicityType[]{TOP_OF_MILLISECOND, TOP_OF_SECOND, TOP_OF_MINUTE, TOP_OF_HOUR, TOP_OF_DAY, TOP_OF_WEEK, TOP_OF_MONTH};
}
