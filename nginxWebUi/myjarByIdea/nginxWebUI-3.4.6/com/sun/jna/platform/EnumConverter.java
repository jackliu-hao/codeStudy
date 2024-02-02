package com.sun.jna.platform;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class EnumConverter<T extends Enum<T>> implements TypeConverter {
   private final Class<T> clazz;

   public EnumConverter(Class<T> clazz) {
      this.clazz = clazz;
   }

   public T fromNative(Object input, FromNativeContext context) {
      Integer i = (Integer)input;
      T[] vals = (Enum[])this.clazz.getEnumConstants();
      return vals[i];
   }

   public Integer toNative(Object input, ToNativeContext context) {
      T t = (Enum)this.clazz.cast(input);
      return t.ordinal();
   }

   public Class<Integer> nativeType() {
      return Integer.class;
   }
}
