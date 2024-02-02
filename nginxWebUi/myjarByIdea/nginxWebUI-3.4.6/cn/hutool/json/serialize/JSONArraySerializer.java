package cn.hutool.json.serialize;

import cn.hutool.json.JSONArray;

@FunctionalInterface
public interface JSONArraySerializer<V> extends JSONSerializer<JSONArray, V> {
}
