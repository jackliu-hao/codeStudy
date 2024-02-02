package ch.qos.logback.core.boolex;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface EventEvaluator<E> extends ContextAware, LifeCycle {
  boolean evaluate(E paramE) throws NullPointerException, EvaluationException;
  
  String getName();
  
  void setName(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\boolex\EventEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */