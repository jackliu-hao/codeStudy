package org.noear.solon.data.cache;

public interface Serializer<T> {
   String name();

   T serialize(Object obj) throws Exception;

   Object deserialize(T dta) throws Exception;
}
