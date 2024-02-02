package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {
   private static final long serialVersionUID = 1L;

   protected AtomicReference<?> convertInternal(Object value) {
      Object targetValue = null;
      Type paramType = TypeUtil.getTypeArgument(AtomicReference.class);
      if (!TypeUtil.isUnknown(paramType)) {
         targetValue = ConverterRegistry.getInstance().convert(paramType, value);
      }

      if (null == targetValue) {
         targetValue = value;
      }

      return new AtomicReference(targetValue);
   }
}
