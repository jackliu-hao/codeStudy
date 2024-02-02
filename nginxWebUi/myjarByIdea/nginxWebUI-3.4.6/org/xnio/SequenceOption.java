package org.xnio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.xnio._private.Messages;

final class SequenceOption<T> extends Option<Sequence<T>> {
   private static final long serialVersionUID = -4328676629293125136L;
   private final transient Class<T> elementType;
   private final transient Option.ValueParser<T> parser;

   SequenceOption(Class<?> declClass, String name, Class<T> elementType) {
      super(declClass, name);
      if (elementType == null) {
         throw Messages.msg.nullParameter("elementType");
      } else {
         this.elementType = elementType;
         this.parser = Option.getParser(elementType);
      }
   }

   public Sequence<T> cast(Object o) {
      if (o == null) {
         return null;
      } else if (o instanceof Sequence) {
         return ((Sequence)o).cast(this.elementType);
      } else if (o instanceof Object[]) {
         return Sequence.of((Object[])((Object[])o)).cast(this.elementType);
      } else if (o instanceof Collection) {
         return Sequence.of((Collection)o).cast(this.elementType);
      } else {
         throw new ClassCastException("Not a sequence");
      }
   }

   public Sequence<T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
      List<T> list = new ArrayList();
      if (string.isEmpty()) {
         return Sequence.empty();
      } else {
         String[] var4 = string.split(",");
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String value = var4[var6];
            list.add(this.parser.parseValue(value, classLoader));
         }

         return Sequence.of((Collection)list);
      }
   }
}
