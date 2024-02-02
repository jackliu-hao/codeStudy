package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.util.Optional;

public class OptionalConverter extends AbstractConverter<Optional<?>> {
   private static final long serialVersionUID = 1L;

   protected Optional<?> convertInternal(Object value) {
      return Optional.ofNullable(value);
   }
}
