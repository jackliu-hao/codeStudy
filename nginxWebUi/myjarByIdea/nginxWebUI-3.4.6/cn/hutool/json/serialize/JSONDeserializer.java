package cn.hutool.json.serialize;

import cn.hutool.json.JSON;

@FunctionalInterface
public interface JSONDeserializer<T> {
   T deserialize(JSON var1);
}
