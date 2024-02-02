package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.lang.Opt;

public class OptConverter extends AbstractConverter<Opt<?>> {
   private static final long serialVersionUID = 1L;

   protected Opt<?> convertInternal(Object value) {
      return Opt.ofNullable(value);
   }
}
