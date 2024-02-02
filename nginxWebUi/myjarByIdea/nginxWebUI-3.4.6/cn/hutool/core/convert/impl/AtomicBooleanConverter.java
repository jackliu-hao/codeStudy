package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.BooleanUtil;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
   private static final long serialVersionUID = 1L;

   protected AtomicBoolean convertInternal(Object value) {
      if (value instanceof Boolean) {
         return new AtomicBoolean((Boolean)value);
      } else {
         String valueStr = this.convertToStr(value);
         return new AtomicBoolean(BooleanUtil.toBoolean(valueStr));
      }
   }
}
