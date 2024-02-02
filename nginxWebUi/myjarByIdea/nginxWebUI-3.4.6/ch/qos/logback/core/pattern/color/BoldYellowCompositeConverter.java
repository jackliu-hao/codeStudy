package ch.qos.logback.core.pattern.color;

public class BoldYellowCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "1;33";
   }
}
