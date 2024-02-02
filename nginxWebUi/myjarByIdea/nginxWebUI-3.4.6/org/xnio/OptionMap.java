package org.xnio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.xnio._private.Messages;

public final class OptionMap implements Iterable<Option<?>>, Serializable {
   private static final long serialVersionUID = 3632842565346928132L;
   private final Map<Option<?>, Object> value;
   public static final OptionMap EMPTY = new OptionMap(Collections.emptyMap());

   private OptionMap(Map<Option<?>, Object> value) {
      this.value = value;
   }

   public boolean contains(Option<?> option) {
      return this.value.containsKey(option);
   }

   public <T> T get(Option<T> option) {
      return option.cast(this.value.get(option));
   }

   public <T> T get(Option<T> option, T defaultValue) {
      Object o = this.value.get(option);
      return o == null ? defaultValue : option.cast(o);
   }

   public boolean get(Option<Boolean> option, boolean defaultValue) {
      Object o = this.value.get(option);
      return o == null ? defaultValue : (Boolean)option.cast(o);
   }

   public int get(Option<Integer> option, int defaultValue) {
      Object o = this.value.get(option);
      return o == null ? defaultValue : (Integer)option.cast(o);
   }

   public long get(Option<Long> option, long defaultValue) {
      Object o = this.value.get(option);
      return o == null ? defaultValue : (Long)option.cast(o);
   }

   public Iterator<Option<?>> iterator() {
      return Collections.unmodifiableCollection(this.value.keySet()).iterator();
   }

   public int size() {
      return this.value.size();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static <T> OptionMap create(Option<T> option, T value) {
      if (option == null) {
         throw Messages.msg.nullParameter("option");
      } else if (value == null) {
         throw Messages.msg.nullParameter("value");
      } else {
         return new OptionMap(Collections.singletonMap(option, option.cast(value)));
      }
   }

   public static <T1, T2> OptionMap create(Option<T1> option1, T1 value1, Option<T2> option2, T2 value2) {
      if (option1 == null) {
         throw Messages.msg.nullParameter("option1");
      } else if (value1 == null) {
         throw Messages.msg.nullParameter("value1");
      } else if (option2 == null) {
         throw Messages.msg.nullParameter("option2");
      } else if (value2 == null) {
         throw Messages.msg.nullParameter("value2");
      } else if (option1 == option2) {
         return create(option2, value2);
      } else {
         IdentityHashMap<Option<?>, Object> map = new IdentityHashMap(2);
         map.put(option1, value1);
         map.put(option2, value2);
         return new OptionMap(map);
      }
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append('{');
      Iterator<Map.Entry<Option<?>, Object>> iterator = this.value.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<Option<?>, Object> entry = (Map.Entry)iterator.next();
         builder.append(entry.getKey()).append("=>").append(entry.getValue());
         if (iterator.hasNext()) {
            builder.append(',');
         }
      }

      builder.append('}');
      return builder.toString();
   }

   public boolean equals(Object other) {
      return other instanceof OptionMap && this.equals((OptionMap)other);
   }

   public boolean equals(OptionMap other) {
      return this == other || other != null && this.value.equals(other.value);
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   // $FF: synthetic method
   OptionMap(Map x0, Object x1) {
      this(x0);
   }

   public static final class Builder {
      private List<OVPair<?>> list;

      private Builder() {
         this.list = new ArrayList();
      }

      public <T> Builder parse(Option<T> key, String stringValue) {
         this.set(key, key.parseValue(stringValue, key.getClass().getClassLoader()));
         return this;
      }

      public <T> Builder parse(Option<T> key, String stringValue, ClassLoader classLoader) {
         this.set(key, key.parseValue(stringValue, classLoader));
         return this;
      }

      public Builder parseAll(Properties props, String prefix, ClassLoader optionClassLoader) {
         if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
         }

         Iterator var4 = props.stringPropertyNames().iterator();

         while(var4.hasNext()) {
            String name = (String)var4.next();
            if (name.startsWith(prefix)) {
               String optionName = name.substring(prefix.length());

               try {
                  Option<?> option = Option.fromString(optionName, optionClassLoader);
                  this.parse(option, props.getProperty(name), optionClassLoader);
               } catch (IllegalArgumentException var8) {
                  Messages.optionParseMsg.invalidOptionInProperty(optionName, name, var8);
               }
            }
         }

         return this;
      }

      public Builder parseAll(Properties props, String prefix) {
         if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
         }

         Iterator var3 = props.stringPropertyNames().iterator();

         while(var3.hasNext()) {
            String name = (String)var3.next();
            if (name.startsWith(prefix)) {
               String optionName = name.substring(prefix.length());

               try {
                  Option<?> option = Option.fromString(optionName, this.getClass().getClassLoader());
                  this.parse(option, props.getProperty(name));
               } catch (IllegalArgumentException var7) {
                  Messages.optionParseMsg.invalidOptionInProperty(optionName, name, var7);
               }
            }
         }

         return this;
      }

      public <T> Builder set(Option<T> key, T value) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else if (value == null) {
            throw Messages.msg.nullParameter("value");
         } else {
            this.list.add(new OVPair(key, value));
            return this;
         }
      }

      public Builder set(Option<Integer> key, int value) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            this.list.add(new OVPair(key, value));
            return this;
         }
      }

      public Builder setSequence(Option<Sequence<Integer>> key, int... values) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            Integer[] a = new Integer[values.length];

            for(int i = 0; i < values.length; ++i) {
               a[i] = values[i];
            }

            this.list.add(new OVPair(key, Sequence.of((Object[])a)));
            return this;
         }
      }

      public Builder set(Option<Long> key, long value) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            this.list.add(new OVPair(key, value));
            return this;
         }
      }

      public Builder setSequence(Option<Sequence<Long>> key, long... values) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            Long[] a = new Long[values.length];

            for(int i = 0; i < values.length; ++i) {
               a[i] = values[i];
            }

            this.list.add(new OVPair(key, Sequence.of((Object[])a)));
            return this;
         }
      }

      public Builder set(Option<Boolean> key, boolean value) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            this.list.add(new OVPair(key, value));
            return this;
         }
      }

      public Builder setSequence(Option<Sequence<Boolean>> key, boolean... values) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            Boolean[] a = new Boolean[values.length];

            for(int i = 0; i < values.length; ++i) {
               a[i] = values[i];
            }

            this.list.add(new OVPair(key, Sequence.of((Object[])a)));
            return this;
         }
      }

      public <T> Builder setSequence(Option<Sequence<T>> key, T... values) {
         if (key == null) {
            throw Messages.msg.nullParameter("key");
         } else {
            this.list.add(new OVPair(key, Sequence.of(values)));
            return this;
         }
      }

      private <T> void copy(Map<?, ?> map, Option<T> option) {
         this.set(option, option.cast(map.get(option)));
      }

      public Builder add(Map<?, ?> map) throws ClassCastException {
         Iterator var2 = map.keySet().iterator();

         while(var2.hasNext()) {
            Object key = var2.next();
            Option<?> option = (Option)Option.class.cast(key);
            this.copy(map, option);
         }

         return this;
      }

      private <T> void copy(OptionMap optionMap, Option<T> option) {
         this.set(option, optionMap.get(option));
      }

      public Builder addAll(OptionMap optionMap) {
         Iterator var2 = optionMap.iterator();

         while(var2.hasNext()) {
            Option<?> option = (Option)var2.next();
            this.copy(optionMap, option);
         }

         return this;
      }

      public OptionMap getMap() {
         List<OVPair<?>> list = this.list;
         if (list.size() == 0) {
            return OptionMap.EMPTY;
         } else if (list.size() == 1) {
            OVPair<?> pair = (OVPair)list.get(0);
            return new OptionMap(Collections.singletonMap(pair.option, pair.value));
         } else {
            Map<Option<?>, Object> map = new IdentityHashMap();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
               OVPair<?> ovPair = (OVPair)var3.next();
               map.put(ovPair.option, ovPair.value);
            }

            return new OptionMap(map);
         }
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }

      private static class OVPair<T> {
         Option<T> option;
         T value;

         private OVPair(Option<T> option, T value) {
            this.option = option;
            this.value = value;
         }

         // $FF: synthetic method
         OVPair(Option x0, Object x1, Object x2) {
            this(x0, x1);
         }
      }
   }
}
