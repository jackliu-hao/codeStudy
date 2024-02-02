package ch.qos.logback.core.pattern.color;

public class BoldMagentaCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
   protected String getForegroundColorCode(E event) {
      return "1;35";
   }
}
