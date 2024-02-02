package cn.hutool.json.serialize;

import cn.hutool.json.JSON;

@FunctionalInterface
public interface JSONSerializer<T extends JSON, V> {
   void serialize(T var1, V var2);
}
