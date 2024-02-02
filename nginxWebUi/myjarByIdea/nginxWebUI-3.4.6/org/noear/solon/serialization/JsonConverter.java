package org.noear.solon.serialization;

@FunctionalInterface
public interface JsonConverter<T> {
   Object convert(T source);
}
