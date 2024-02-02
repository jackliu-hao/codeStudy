package cn.hutool.extra.expression.engine.aviator;

import cn.hutool.extra.expression.ExpressionEngine;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import java.util.Map;

public class AviatorEngine implements ExpressionEngine {
   private final AviatorEvaluatorInstance engine = AviatorEvaluator.getInstance();

   public Object eval(String expression, Map<String, Object> context) {
      return this.engine.execute(expression, context);
   }

   public AviatorEvaluatorInstance getEngine() {
      return this.engine;
   }
}
