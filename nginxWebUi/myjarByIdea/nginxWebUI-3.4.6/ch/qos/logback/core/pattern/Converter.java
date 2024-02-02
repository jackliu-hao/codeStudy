package ch.qos.logback.core.pattern;

public abstract class Converter<E> {
   Converter<E> next;

   public abstract String convert(E var1);

   public void write(StringBuilder buf, E event) {
      buf.append(this.convert(event));
   }

   public final void setNext(Converter<E> next) {
      if (this.next != null) {
         throw new IllegalStateException("Next converter has been already set");
      } else {
         this.next = next;
      }
   }

   public final Converter<E> getNext() {
      return this.next;
   }
}
