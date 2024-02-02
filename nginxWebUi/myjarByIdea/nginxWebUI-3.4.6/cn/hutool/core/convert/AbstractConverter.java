package cn.hutool.core.convert;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Map;

public abstract class AbstractConverter<T> implements Converter<T>, Serializable {
   private static final long serialVersionUID = 1L;

   public T convertQuietly(Object value, T defaultValue) {
      try {
         return this.convert(value, defaultValue);
      } catch (Exception var4) {
         return defaultValue;
      }
   }

   public T convert(Object value, T defaultValue) {
      Class<T> targetType = this.getTargetType();
      if (null == targetType && null == defaultValue) {
         throw new NullPointerException(StrUtil.format("[type] and [defaultValue] are both null for Converter [{}], we can not know what type to convert !", new Object[]{this.getClass().getName()}));
      } else {
         if (null == targetType) {
            targetType = defaultValue.getClass();
         }

         if (null == value) {
            return defaultValue;
         } else if (null != defaultValue && !targetType.isInstance(defaultValue)) {
            throw new IllegalArgumentException(StrUtil.format("Default value [{}]({}) is not the instance of [{}]", new Object[]{defaultValue, defaultValue.getClass(), targetType}));
         } else if (targetType.isInstance(value) && !Map.class.isAssignableFrom(targetType)) {
            return targetType.cast(value);
         } else {
            T result = this.convertInternal(value);
            return null == result ? defaultValue : result;
         }
      }
   }

   protected abstract T convertInternal(Object var1);

   protected String convertToStr(Object value) {
      if (null == value) {
         return null;
      } else if (value instanceof CharSequence) {
         return value.toString();
      } else if (ArrayUtil.isArray(value)) {
         return ArrayUtil.toString(value);
      } else {
         return CharUtil.isChar(value) ? CharUtil.toString((Character)value) : value.toString();
      }
   }

   public Class<T> getTargetType() {
      return ClassUtil.getTypeArgument(this.getClass());
   }
}
