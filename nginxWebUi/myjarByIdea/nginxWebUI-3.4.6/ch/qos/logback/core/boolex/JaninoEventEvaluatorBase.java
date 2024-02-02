package ch.qos.logback.core.boolex;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.janino.ScriptEvaluator;

public abstract class JaninoEventEvaluatorBase<E> extends EventEvaluatorBase<E> {
   static Class<?> EXPRESSION_TYPE;
   static Class<?>[] THROWN_EXCEPTIONS;
   public static final int ERROR_THRESHOLD = 4;
   private String expression;
   ScriptEvaluator scriptEvaluator;
   private int errorCount = 0;
   protected List<Matcher> matcherList = new ArrayList();

   protected abstract String getDecoratedExpression();

   protected abstract String[] getParameterNames();

   protected abstract Class<?>[] getParameterTypes();

   protected abstract Object[] getParameterValues(E var1);

   public void start() {
      try {
         assert this.context != null;

         this.scriptEvaluator = new ScriptEvaluator(this.getDecoratedExpression(), EXPRESSION_TYPE, this.getParameterNames(), this.getParameterTypes(), THROWN_EXCEPTIONS);
         super.start();
      } catch (Exception var2) {
         this.addError("Could not start evaluator with expression [" + this.expression + "]", var2);
      }

   }

   public boolean evaluate(E event) throws EvaluationException {
      if (!this.isStarted()) {
         throw new IllegalStateException("Evaluator [" + this.name + "] was called in stopped state");
      } else {
         try {
            Boolean result = (Boolean)this.scriptEvaluator.evaluate(this.getParameterValues(event));
            return result;
         } catch (Exception var3) {
            ++this.errorCount;
            if (this.errorCount >= 4) {
               this.stop();
            }

            throw new EvaluationException("Evaluator [" + this.name + "] caused an exception", var3);
         }
      }
   }

   public String getExpression() {
      return this.expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public void addMatcher(Matcher matcher) {
      this.matcherList.add(matcher);
   }

   public List<Matcher> getMatcherList() {
      return this.matcherList;
   }

   static {
      EXPRESSION_TYPE = Boolean.TYPE;
      THROWN_EXCEPTIONS = new Class[1];
      THROWN_EXCEPTIONS[0] = EvaluationException.class;
   }
}
