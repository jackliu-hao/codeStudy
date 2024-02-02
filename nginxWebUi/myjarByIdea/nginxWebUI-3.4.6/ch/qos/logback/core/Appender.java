package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.LifeCycle;

public interface Appender<E> extends LifeCycle, ContextAware, FilterAttachable<E> {
   String getName();

   void doAppend(E var1) throws LogbackException;

   void setName(String var1);
}
