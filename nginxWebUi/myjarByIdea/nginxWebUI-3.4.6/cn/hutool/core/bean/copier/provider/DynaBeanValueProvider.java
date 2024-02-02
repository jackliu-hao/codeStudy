package cn.hutool.core.bean.copier.provider;

import cn.hutool.core.bean.DynaBean;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.convert.Convert;
import java.lang.reflect.Type;

public class DynaBeanValueProvider implements ValueProvider<String> {
   private final DynaBean dynaBean;
   private final boolean ignoreError;

   public DynaBeanValueProvider(DynaBean dynaBean, boolean ignoreError) {
      this.dynaBean = dynaBean;
      this.ignoreError = ignoreError;
   }

   public Object value(String key, Type valueType) {
      Object value = this.dynaBean.get(key);
      return Convert.convertWithCheck(valueType, value, (Object)null, this.ignoreError);
   }

   public boolean containsKey(String key) {
      return this.dynaBean.containsProp(key);
   }
}
