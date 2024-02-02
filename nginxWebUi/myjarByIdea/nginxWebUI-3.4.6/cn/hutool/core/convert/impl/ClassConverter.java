package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.ClassLoaderUtil;

public class ClassConverter extends AbstractConverter<Class<?>> {
   private static final long serialVersionUID = 1L;
   private final boolean isInitialized;

   public ClassConverter() {
      this(true);
   }

   public ClassConverter(boolean isInitialized) {
      this.isInitialized = isInitialized;
   }

   protected Class<?> convertInternal(Object value) {
      return ClassLoaderUtil.loadClass(this.convertToStr(value), this.isInitialized);
   }
}
