package ch.qos.logback.core.spi;

import java.io.Serializable;

public interface PreSerializationTransformer<E> {
   Serializable transform(E var1);
}
