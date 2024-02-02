package ch.qos.logback.core.pattern;

public final class LiteralConverter<E> extends Converter<E> {
   String literal;

   public LiteralConverter(String literal) {
      this.literal = literal;
   }

   public String convert(E o) {
      return this.literal;
   }
}
