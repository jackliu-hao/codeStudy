package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicLongArrayConverter extends AbstractConverter<AtomicLongArray> {
   private static final long serialVersionUID = 1L;

   protected AtomicLongArray convertInternal(Object value) {
      return new AtomicLongArray((long[])Convert.convert(long[].class, value));
   }
}
