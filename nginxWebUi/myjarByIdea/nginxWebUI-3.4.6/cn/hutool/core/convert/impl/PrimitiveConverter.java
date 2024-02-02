package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.function.Function;

public class PrimitiveConverter extends AbstractConverter<Object> {
   private static final long serialVersionUID = 1L;
   private final Class<?> targetType;

   public PrimitiveConverter(Class<?> clazz) {
      if (null == clazz) {
         throw new NullPointerException("PrimitiveConverter not allow null target type!");
      } else if (!clazz.isPrimitive()) {
         throw new IllegalArgumentException("[" + clazz + "] is not a primitive class!");
      } else {
         this.targetType = clazz;
      }
   }

   protected Object convertInternal(Object value) {
      return convert(value, this.targetType, this::convertToStr);
   }

   protected String convertToStr(Object value) {
      return StrUtil.trim(super.convertToStr(value));
   }

   public Class<Object> getTargetType() {
      return this.targetType;
   }

   protected static Object convert(Object value, Class<?> primitiveClass, Function<Object, String> toStringFunc) {
      if (Byte.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Byte.class, toStringFunc), (int)0);
      } else if (Short.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Short.class, toStringFunc), (int)0);
      } else if (Integer.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Integer.class, toStringFunc), (int)0);
      } else if (Long.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Long.class, toStringFunc), (int)0);
      } else if (Float.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Float.class, toStringFunc), (int)0);
      } else if (Double.TYPE == primitiveClass) {
         return ObjectUtil.defaultIfNull(NumberConverter.convert(value, Double.class, toStringFunc), (int)0);
      } else if (Character.TYPE == primitiveClass) {
         return Convert.convert(Character.class, value);
      } else if (Boolean.TYPE == primitiveClass) {
         return Convert.convert(Boolean.class, value);
      } else {
         throw new ConvertException("Unsupported target type: {}", new Object[]{primitiveClass});
      }
   }
}
