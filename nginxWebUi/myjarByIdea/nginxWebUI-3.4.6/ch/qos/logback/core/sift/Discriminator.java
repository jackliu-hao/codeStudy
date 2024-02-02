package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.LifeCycle;

public interface Discriminator<E> extends LifeCycle {
   String getDiscriminatingValue(E var1);

   String getKey();
}
