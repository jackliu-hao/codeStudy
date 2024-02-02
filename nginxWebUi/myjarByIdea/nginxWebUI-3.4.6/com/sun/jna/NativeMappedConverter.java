package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public class NativeMappedConverter implements TypeConverter {
   private static final Map<Class<?>, Reference<NativeMappedConverter>> converters = new WeakHashMap();
   private final Class<?> type;
   private final Class<?> nativeType;
   private final NativeMapped instance;

   public static NativeMappedConverter getInstance(Class<?> cls) {
      synchronized(converters) {
         Reference<NativeMappedConverter> r = (Reference)converters.get(cls);
         NativeMappedConverter nmc = r != null ? (NativeMappedConverter)r.get() : null;
         if (nmc == null) {
            nmc = new NativeMappedConverter(cls);
            converters.put(cls, new SoftReference(nmc));
         }

         return nmc;
      }
   }

   public NativeMappedConverter(Class<?> type) {
      if (!NativeMapped.class.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
      } else {
         this.type = type;
         this.instance = this.defaultValue();
         this.nativeType = this.instance.nativeType();
      }
   }

   public NativeMapped defaultValue() {
      return this.type.isEnum() ? (NativeMapped)this.type.getEnumConstants()[0] : (NativeMapped)Klass.newInstance(this.type);
   }

   public Object fromNative(Object nativeValue, FromNativeContext context) {
      return this.instance.fromNative(nativeValue, context);
   }

   public Class<?> nativeType() {
      return this.nativeType;
   }

   public Object toNative(Object value, ToNativeContext context) {
      if (value == null) {
         if (Pointer.class.isAssignableFrom(this.nativeType)) {
            return null;
         }

         value = this.defaultValue();
      }

      return ((NativeMapped)value).toNative();
   }
}
