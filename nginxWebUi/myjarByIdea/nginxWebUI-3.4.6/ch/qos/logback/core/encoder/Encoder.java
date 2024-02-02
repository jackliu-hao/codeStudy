package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Encoder<E> extends ContextAware, LifeCycle {
   byte[] headerBytes();

   byte[] encode(E var1);

   byte[] footerBytes();
}
