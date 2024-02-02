package org.xnio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.xnio._private.Messages;

final class TypeSequenceOption<T> extends Option<Sequence<Class<? extends T>>> {
   private static final long serialVersionUID = -4328676629293125136L;
   private final transient Class<T> elementDeclType;
   private final transient Option.ValueParser<Class<? extends T>> parser;

   TypeSequenceOption(Class<?> declClass, String name, Class<T> elementDeclType) {
      super(declClass, name);
      if (elementDeclType == null) {
         throw Messages.msg.nullParameter("elementDeclType");
      } else {
         this.elementDeclType = elementDeclType;
         this.parser = Option.getClassParser(elementDeclType);
      }
   }

   public Sequence<Class<? extends T>> cast(Object o) {
      if (o == null) {
         return null;
      } else if (o instanceof Sequence) {
         return castSeq((Sequence)o, this.elementDeclType);
      } else if (o instanceof Object[]) {
         return castSeq(Sequence.of((Object[])((Object[])o)), this.elementDeclType);
      } else if (o instanceof Collection) {
         return castSeq(Sequence.of((Collection)o), this.elementDeclType);
      } else {
         throw new ClassCastException("Not a sequence");
      }
   }

   static <T> Sequence<Class<? extends T>> castSeq(Sequence<?> seq, Class<T> type) {
      Iterator var2 = seq.iterator();

      while(var2.hasNext()) {
         Object o = var2.next();
         ((Class)o).asSubclass(type);
      }

      return seq;
   }

   public Sequence<Class<? extends T>> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
      List<Class<? extends T>> list = new ArrayList();
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
