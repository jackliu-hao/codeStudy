package cn.hutool.core.clone;

import cn.hutool.core.util.ReflectUtil;

public interface DefaultCloneable<T> extends java.lang.Cloneable {
   default T clone0() {
      try {
         return ReflectUtil.invoke(this, (String)"clone");
      } catch (Exception var2) {
         throw new CloneRuntimeException(var2);
      }
   }
}
