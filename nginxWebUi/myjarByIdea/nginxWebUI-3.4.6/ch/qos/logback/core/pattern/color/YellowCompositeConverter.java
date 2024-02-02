package ch.qos.logback.core.pattern.color;

public class YellowCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "33";
   }
}
