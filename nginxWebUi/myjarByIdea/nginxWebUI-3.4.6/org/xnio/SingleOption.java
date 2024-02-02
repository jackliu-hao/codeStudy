package org.xnio;

import org.xnio._private.Messages;

final class SingleOption<T> extends Option<T> {
   private static final long serialVersionUID = 2449094406108952764L;
   private final transient Class<T> type;
   private final transient Option.ValueParser<T> parser;

   SingleOption(Class<?> declClass, String name, Class<T> type) {
      super(declClass, name);
      if (type == null) {
         throw Messages.msg.nullParameter("type");
      } else {
         this.type = type;
         this.parser = Option.getParser(type);
      }
   }

   public T cast(Object o) {
      return this.type.cast(o);
   }

   public T parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
      return this.parser.parseValue(string, classLoader);
   }
}
