package org.xnio;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xnio._private.Messages;

public abstract class Option<T> implements Serializable {
   private static final long serialVersionUID = -1564427329140182760L;
   private final Class<?> declClass;
   private final String name;
   private static final Map<Class<?>, ValueParser<?>> parsers;
   private static final ValueParser<?> noParser = new ValueParser<Object>() {
      public Object parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
         throw Messages.msg.noOptionParser();
      }
   };

   Option(Class<?> declClass, String name) {
      if (declClass == null) {
         throw Messages.msg.nullParameter("declClass");
      } else if (name == null) {
         throw Messages.msg.nullParameter("name");
      } else {
         this.declClass = declClass;
         this.name = name;
      }
   }

   public static <T> Option<T> simple(Class<?> declClass, String name, Class<T> type) {
      return new SingleOption(declClass, name, type);
   }

   public static <T> Option<Sequence<T>> sequence(Class<?> declClass, String name, Class<T> elementType) {
      return new SequenceOption(declClass, name, elementType);
   }

   public static <T> Option<Class<? extends T>> type(Class<?> declClass, String name, Class<T> declType) {
      return new TypeOption(declClass, name, declType);
   }

   public static <T> Option<Sequence<Class<? extends T>>> typeSequence(Class<?> declClass, String name, Class<T> elementDeclType) {
      return new TypeSequenceOption(declClass, name, elementDeclType);
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.declClass.getName() + "." + this.name;
   }

   public static Option<?> fromString(String name, ClassLoader classLoader) throws IllegalArgumentException {
      int lastDot = name.lastIndexOf(46);
      if (lastDot == -1) {
         throw Messages.msg.invalidOptionName(name);
      } else {
         String fieldName = name.substring(lastDot + 1);
         String className = name.substring(0, lastDot);

         Class clazz;
         try {
            clazz = Class.forName(className, true, classLoader);
         } catch (ClassNotFoundException var12) {
            throw Messages.msg.optionClassNotFound(className, classLoader);
         }

         Field field;
         try {
            field = clazz.getField(fieldName);
         } catch (NoSuchFieldException var11) {
            throw Messages.msg.noField(fieldName, clazz);
         }

         int modifiers = field.getModifiers();
         if (!Modifier.isPublic(modifiers)) {
            throw Messages.msg.fieldNotAccessible(fieldName, clazz);
         } else if (!Modifier.isStatic(modifiers)) {
            throw Messages.msg.fieldNotStatic(fieldName, clazz);
         } else {
            Option option;
            try {
               option = (Option)field.get((Object)null);
            } catch (IllegalAccessException var10) {
               throw Messages.msg.fieldNotAccessible(fieldName, clazz);
            }

            if (option == null) {
               throw Messages.msg.invalidNullOption(name);
            } else {
               return option;
            }
         }
      }
   }

   public abstract T cast(Object var1) throws ClassCastException;

   public final T cast(Object o, T defaultVal) throws ClassCastException {
      return o == null ? defaultVal : this.cast(o);
   }

   public abstract T parseValue(String var1, ClassLoader var2) throws IllegalArgumentException;

   protected final Object readResolve() throws ObjectStreamException {
      try {
         Field field = this.declClass.getField(this.name);
         int modifiers = field.getModifiers();
         if (!Modifier.isPublic(modifiers)) {
            throw new InvalidObjectException("Invalid Option instance (the field is not public)");
         } else if (!Modifier.isStatic(modifiers)) {
            throw new InvalidObjectException("Invalid Option instance (the field is not static)");
         } else {
            Option<?> option = (Option)field.get((Object)null);
            if (option == null) {
               throw new InvalidObjectException("Invalid null Option");
            } else {
               return option;
            }
         }
      } catch (NoSuchFieldException var4) {
         throw new InvalidObjectException("Invalid Option instance (no matching field)");
      } catch (IllegalAccessException var5) {
         throw new InvalidObjectException("Invalid Option instance (Illegal access on field get)");
      }
   }

   public static SetBuilder setBuilder() {
      return new SetBuilder();
   }

   static <T> ValueParser<Class<? extends T>> getClassParser(final Class<T> argType) {
      return new ValueParser<Class<? extends T>>() {
         public Class<? extends T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            try {
               return Class.forName(string, false, classLoader).asSubclass(argType);
            } catch (ClassNotFoundException var4) {
               throw Messages.msg.classNotFound(string, var4);
            } catch (ClassCastException var5) {
               throw Messages.msg.classNotInstance(string, argType);
            }
         }
      };
   }

   static <T, E extends Enum<E>> ValueParser<T> getEnumParser(final Class<T> enumType) {
      return new ValueParser<T>() {
         public T parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return enumType.cast(Enum.valueOf(Option.asEnum(enumType), string.trim()));
         }
      };
   }

   private static <T, E extends Enum<E>> Class<E> asEnum(Class<T> enumType) {
      return enumType;
   }

   static <T> ValueParser<T> getParser(Class<T> argType) {
      if (argType.isEnum()) {
         return getEnumParser(argType);
      } else {
         ValueParser<?> value = (ValueParser)parsers.get(argType);
         return value == null ? noParser : value;
      }
   }

   static {
      Map<Class<?>, ValueParser<?>> map = new HashMap();
      map.put(Byte.class, new ValueParser<Byte>() {
         public Byte parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return Byte.decode(string.trim());
         }
      });
      map.put(Short.class, new ValueParser<Short>() {
         public Short parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return Short.decode(string.trim());
         }
      });
      map.put(Integer.class, new ValueParser<Integer>() {
         public Integer parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return Integer.decode(string.trim());
         }
      });
      map.put(Long.class, new ValueParser<Long>() {
         public Long parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return Long.decode(string.trim());
         }
      });
      map.put(String.class, new ValueParser<String>() {
         public String parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return string.trim();
         }
      });
      map.put(Boolean.class, new ValueParser<Boolean>() {
         public Boolean parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            return Boolean.valueOf(string.trim());
         }
      });
      map.put(Property.class, new ValueParser<Object>() {
         public Object parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
            int idx = string.indexOf(61);
            if (idx == -1) {
               throw Messages.msg.invalidOptionPropertyFormat(string);
            } else {
               return Property.of(string.substring(0, idx), string.substring(idx + 1, string.length()));
            }
         }
      });
      parsers = map;
   }

   interface ValueParser<T> {
      T parseValue(String var1, ClassLoader var2) throws IllegalArgumentException;
   }

   public static class SetBuilder {
      private List<Option<?>> optionSet = new ArrayList();

      SetBuilder() {
      }

      public SetBuilder add(Option<?> option) {
         if (option == null) {
            throw Messages.msg.nullParameter("option");
         } else {
            this.optionSet.add(option);
            return this;
         }
      }

      public SetBuilder add(Option<?> option1, Option<?> option2) {
         if (option1 == null) {
            throw Messages.msg.nullParameter("option1");
         } else if (option2 == null) {
            throw Messages.msg.nullParameter("option2");
         } else {
            this.optionSet.add(option1);
            this.optionSet.add(option2);
            return this;
         }
      }

      public SetBuilder add(Option<?> option1, Option<?> option2, Option<?> option3) {
         if (option1 == null) {
            throw Messages.msg.nullParameter("option1");
         } else if (option2 == null) {
            throw Messages.msg.nullParameter("option2");
         } else if (option3 == null) {
            throw Messages.msg.nullParameter("option3");
         } else {
            this.optionSet.add(option1);
            this.optionSet.add(option2);
            this.optionSet.add(option3);
            return this;
         }
      }

      public SetBuilder add(Option<?>... options) {
         if (options == null) {
            throw Messages.msg.nullParameter("options");
         } else {
            Option[] var2 = options;
            int var3 = options.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Option<?> option = var2[var4];
               this.add(option);
            }

            return this;
         }
      }

      public SetBuilder addAll(Collection<Option<?>> options) {
         if (options == null) {
            throw Messages.msg.nullParameter("option");
         } else {
            Iterator var2 = options.iterator();

            while(var2.hasNext()) {
               Option<?> option = (Option)var2.next();
               this.add(option);
            }

            return this;
         }
      }

      public Set<Option<?>> create() {
         return Collections.unmodifiableSet(new LinkedHashSet(this.optionSet));
      }
   }
}
