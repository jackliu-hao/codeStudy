package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.FormatInfo;

public class IntegerTokenConverter extends DynamicConverter<Object> implements MonoTypedConverter {
   public static final String CONVERTER_KEY = "i";

   public String convert(int i) {
      String s = Integer.toString(i);
      FormatInfo formattingInfo = this.getFormattingInfo();
      if (formattingInfo == null) {
         return s;
      } else {
         int min = formattingInfo.getMin();
         StringBuilder sbuf = new StringBuilder();

         for(int j = s.length(); j < min; ++j) {
            sbuf.append('0');
         }

         return sbuf.append(s).toString();
      }
   }

   public String convert(Object o) {
      if (o == null) {
         throw new IllegalArgumentException("Null argument forbidden");
      } else if (o instanceof Integer) {
         Integer i = (Integer)o;
         return this.convert(i);
      } else {
         throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
      }
   }

   public boolean isApplicable(Object o) {
      return o instanceof Integer;
   }
}
