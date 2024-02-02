package cn.hutool.json.serialize;

import cn.hutool.json.JSONObject;

@FunctionalInterface
public interface JSONObjectSerializer<V> extends JSONSerializer<JSONObject, V> {
}
