package ch.qos.logback.core.boolex;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface EventEvaluator<E> extends ContextAware, LifeCycle {
   boolean evaluate(E var1) throws NullPointerException, EvaluationException;

   String getName();

   void setName(String var1);
}
