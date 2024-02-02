package cn.hutool.extra.expression.engine.mvel;

import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.mvel2.MVEL;

public class MvelEngine implements ExpressionEngine {
   public Object eval(String expression, Map<String, Object> context) {
      return MVEL.eval(expression, context);
   }
}
