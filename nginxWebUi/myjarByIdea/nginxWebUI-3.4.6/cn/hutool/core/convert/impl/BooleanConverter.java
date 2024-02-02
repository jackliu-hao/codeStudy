package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.BooleanUtil;

public class BooleanConverter extends AbstractConverter<Boolean> {
   private static final long serialVersionUID = 1L;

   protected Boolean convertInternal(Object value) {
      return value instanceof Number ? 0.0 != ((Number)value).doubleValue() : BooleanUtil.toBoolean(this.convertToStr(value));
   }
}
