package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;

public class PrefixCompositeConverter extends CompositeConverter<ILoggingEvent> {
   public String convert(ILoggingEvent event) {
      StringBuilder buf = new StringBuilder();
      Converter<ILoggingEvent> childConverter = this.getChildConverter();

      for(Converter<ILoggingEvent> c = childConverter; c != null; c = c.getNext()) {
         String key;
         if (c instanceof MDCConverter) {
            MDCConverter mdcConverter = (MDCConverter)c;
            key = mdcConverter.getKey();
            if (key != null) {
               buf.append(key).append("=");
            }
         } else if (c instanceof PropertyConverter) {
            PropertyConverter pc = (PropertyConverter)c;
            key = pc.getKey();
            if (key != null) {
               buf.append(key).append("=");
            }
         } else {
            String classOfConverter = c.getClass().getName();
            key = (String)PatternLayout.CONVERTER_CLASS_TO_KEY_MAP.get(classOfConverter);
            if (key != null) {
               buf.append(key).append("=");
            }
         }

         buf.append(c.convert(event));
      }

      return buf.toString();
   }

   protected String transform(ILoggingEvent event, String in) {
      throw new UnsupportedOperationException();
   }
}
