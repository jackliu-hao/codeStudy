package ch.qos.logback.core.pattern;

public class IdentityCompositeConverter<E> extends CompositeConverter<E> {
   protected String transform(E event, String in) {
      return in;
   }
}
