package freemarker.core;

public class _DelayedOrdinal extends _DelayedConversionToString {
   public _DelayedOrdinal(Object object) {
      super(object);
   }

   protected String doConversion(Object obj) {
      if (obj instanceof Number) {
         long n = ((Number)obj).longValue();
         if (n % 10L == 1L && n % 100L != 11L) {
            return n + "st";
         } else if (n % 10L == 2L && n % 100L != 12L) {
            return n + "nd";
         } else {
            return n % 10L == 3L && n % 100L != 13L ? n + "rd" : n + "th";
         }
      } else {
         return "" + obj;
      }
   }
}
