package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;

public class CastConverter<T> extends AbstractConverter<T> {
   private static final long serialVersionUID = 1L;
   private Class<T> targetType;

   protected T convertInternal(Object value) {
      throw new ConvertException("Can not cast value to [{}]", new Object[]{this.targetType});
   }

   public Class<T> getTargetType() {
      return this.targetType;
   }
}
