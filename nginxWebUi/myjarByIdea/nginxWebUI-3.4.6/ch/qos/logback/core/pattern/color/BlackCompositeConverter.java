package ch.qos.logback.core.pattern.color;

public class BlackCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "30";
   }
}
