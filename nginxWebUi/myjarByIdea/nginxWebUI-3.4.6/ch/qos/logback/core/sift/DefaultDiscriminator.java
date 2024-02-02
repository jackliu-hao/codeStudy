package ch.qos.logback.core.sift;

public class DefaultDiscriminator<E> extends AbstractDiscriminator<E> {
   public static final String DEFAULT = "default";

   public String getDiscriminatingValue(E e) {
      return "default";
   }

   public String getKey() {
      return "default";
   }
}
