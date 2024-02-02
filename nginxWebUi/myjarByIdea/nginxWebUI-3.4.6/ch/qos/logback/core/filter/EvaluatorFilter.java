package ch.qos.logback.core.filter;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.FilterReply;

public class EvaluatorFilter<E> extends AbstractMatcherFilter<E> {
   EventEvaluator<E> evaluator;

   public void start() {
      if (this.evaluator != null) {
         super.start();
      } else {
         this.addError("No evaluator set for filter " + this.getName());
      }

   }

   public EventEvaluator<E> getEvaluator() {
      return this.evaluator;
   }

   public void setEvaluator(EventEvaluator<E> evaluator) {
      this.evaluator = evaluator;
   }

   public FilterReply decide(E event) {
      if (this.isStarted() && this.evaluator.isStarted()) {
         try {
            return this.evaluator.evaluate(event) ? this.onMatch : this.onMismatch;
         } catch (EvaluationException var3) {
            this.addError("Evaluator " + this.evaluator.getName() + " threw an exception", var3);
            return FilterReply.NEUTRAL;
         }
      } else {
         return FilterReply.NEUTRAL;
      }
   }
}
