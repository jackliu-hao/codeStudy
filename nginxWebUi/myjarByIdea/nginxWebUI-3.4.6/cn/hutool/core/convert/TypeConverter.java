package cn.hutool.core.convert;

import java.lang.reflect.Type;

@FunctionalInterface
public interface TypeConverter {
   Object convert(Type var1, Object var2);
}
