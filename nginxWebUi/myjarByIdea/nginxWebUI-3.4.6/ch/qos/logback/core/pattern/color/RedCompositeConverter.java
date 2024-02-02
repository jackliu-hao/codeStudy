package ch.qos.logback.core.pattern.color;

public class RedCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "31";
   }
}
