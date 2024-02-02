package org.xnio;

import org.xnio._private.Messages;

final class TypeOption<T> extends Option<Class<? extends T>> {
   private static final long serialVersionUID = 2449094406108952764L;
   private final transient Class<T> type;
   private final transient Option.ValueParser<Class<? extends T>> parser;

   TypeOption(Class<?> declClass, String name, Class<T> type) {
      super(declClass, name);
      if (type == null) {
         throw Messages.msg.nullParameter("type");
      } else {
         this.type = type;
         this.parser = Option.getClassParser(type);
      }
   }

   public Class<? extends T> cast(Object o) {
      return ((Class)o).asSubclass(this.type);
   }

   public Class<? extends T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
      return (Class)this.parser.parseValue(string, classLoader);
   }
}
