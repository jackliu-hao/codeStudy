package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction;

public class EvaluatorAction extends AbstractEventEvaluatorAction {
   protected String defaultClassName() {
      return JaninoEventEvaluator.class.getName();
   }
}
