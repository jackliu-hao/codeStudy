package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.util.UUID;

public class UUIDConverter extends AbstractConverter<UUID> {
   private static final long serialVersionUID = 1L;

   protected UUID convertInternal(Object value) {
      return UUID.fromString(this.convertToStr(value));
   }
}
