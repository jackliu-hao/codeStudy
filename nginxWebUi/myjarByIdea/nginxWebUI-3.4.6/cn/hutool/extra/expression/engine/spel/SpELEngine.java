package cn.hutool.extra.expression.engine.spel;

import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELEngine implements ExpressionEngine {
   private final ExpressionParser parser = new SpelExpressionParser();

   public Object eval(String expression, Map<String, Object> context) {
      EvaluationContext evaluationContext = new StandardEvaluationContext();
      context.forEach(evaluationContext::setVariable);
      return this.parser.parseExpression(expression).getValue(evaluationContext);
   }
}
