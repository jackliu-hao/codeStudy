package cn.hutool.core.comparator;

import cn.hutool.core.bean.BeanUtil;

public class PropertyComparator<T> extends FuncComparator<T> {
   private static final long serialVersionUID = 9157326766723846313L;

   public PropertyComparator(String property) {
      this(property, true);
   }

   public PropertyComparator(String property, boolean isNullGreater) {
      super(isNullGreater, (bean) -> {
         return (Comparable)BeanUtil.getProperty(bean, property);
      });
   }
}
