package cn.hutool.core.convert;

public interface Converter<T> {
   T convert(Object var1, T var2) throws IllegalArgumentException;

   default T convertWithCheck(Object value, T defaultValue, boolean quietly) {
      try {
         return this.convert(value, defaultValue);
      } catch (Exception var5) {
         if (quietly) {
            return defaultValue;
         } else {
            throw var5;
         }
      }
   }
}
