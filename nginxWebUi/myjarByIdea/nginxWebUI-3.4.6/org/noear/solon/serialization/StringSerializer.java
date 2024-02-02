package org.noear.solon.serialization;

import java.io.IOException;

@FunctionalInterface
public interface StringSerializer<T> {
   String serialize(T source) throws IOException;
}
