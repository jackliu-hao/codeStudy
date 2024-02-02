package cn.hutool.core.bean.copier;

import java.lang.reflect.Type;

public interface ValueProvider<T> {
   Object value(T var1, Type var2);

   boolean containsKey(T var1);
}
