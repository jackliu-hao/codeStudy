package ch.qos.logback.core.pattern.color;

public class WhiteCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "37";
   }
}
