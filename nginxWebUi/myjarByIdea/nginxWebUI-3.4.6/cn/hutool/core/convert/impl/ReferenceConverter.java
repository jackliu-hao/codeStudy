package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

public class ReferenceConverter extends AbstractConverter<Reference> {
   private static final long serialVersionUID = 1L;
   private final Class<? extends Reference> targetType;

   public ReferenceConverter(Class<? extends Reference> targetType) {
      this.targetType = targetType;
   }

   protected Reference<?> convertInternal(Object value) {
      Object targetValue = null;
      Type paramType = TypeUtil.getTypeArgument(this.targetType);
      if (!TypeUtil.isUnknown(paramType)) {
         targetValue = ConverterRegistry.getInstance().convert(paramType, value);
      }

      if (null == targetValue) {
         targetValue = value;
      }

      if (this.targetType == WeakReference.class) {
         return new WeakReference(targetValue);
      } else if (this.targetType == SoftReference.class) {
         return new SoftReference(targetValue);
      } else {
         throw new UnsupportedOperationException(StrUtil.format("Unsupport Reference type: {}", new Object[]{this.targetType.getName()}));
      }
   }
}
