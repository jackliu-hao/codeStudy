package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;

public class ConverterUtil {
   public static <E> void startConverters(Converter<E> head) {
      for(Converter<E> c = head; c != null; c = c.getNext()) {
         if (c instanceof CompositeConverter) {
            CompositeConverter<E> cc = (CompositeConverter)c;
            Converter<E> childConverter = cc.childConverter;
            startConverters(childConverter);
            cc.start();
         } else if (c instanceof DynamicConverter) {
            DynamicConverter<E> dc = (DynamicConverter)c;
            dc.start();
         }
      }

   }

   public static <E> Converter<E> findTail(Converter<E> head) {
      Converter p;
      Converter next;
      for(p = head; p != null; p = next) {
         next = p.getNext();
         if (next == null) {
            break;
         }
      }

      return p;
   }

   public static <E> void setContextForConverters(Context context, Converter<E> head) {
      for(Converter<E> c = head; c != null; c = c.getNext()) {
         if (c instanceof ContextAware) {
            ((ContextAware)c).setContext(context);
         }
      }

   }
}
