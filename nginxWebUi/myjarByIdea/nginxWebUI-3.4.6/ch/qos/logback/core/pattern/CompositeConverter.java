package ch.qos.logback.core.pattern;

public abstract class CompositeConverter<E> extends DynamicConverter<E> {
   Converter<E> childConverter;

   public String convert(E event) {
      StringBuilder buf = new StringBuilder();

      for(Converter<E> c = this.childConverter; c != null; c = c.next) {
         c.write(buf, event);
      }

      String intermediary = buf.toString();
      return this.transform(event, intermediary);
   }

   protected abstract String transform(E var1, String var2);

   public Converter<E> getChildConverter() {
      return this.childConverter;
   }

   public void setChildConverter(Converter<E> child) {
      this.childConverter = child;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("CompositeConverter<");
      if (this.formattingInfo != null) {
         buf.append(this.formattingInfo);
      }

      if (this.childConverter != null) {
         buf.append(", children: ").append(this.childConverter);
      }

      buf.append(">");
      return buf.toString();
   }
}
