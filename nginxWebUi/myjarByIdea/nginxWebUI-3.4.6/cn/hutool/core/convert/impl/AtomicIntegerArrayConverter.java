package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayConverter extends AbstractConverter<AtomicIntegerArray> {
   private static final long serialVersionUID = 1L;

   protected AtomicIntegerArray convertInternal(Object value) {
      return new AtomicIntegerArray((int[])Convert.convert(int[].class, value));
   }
}
