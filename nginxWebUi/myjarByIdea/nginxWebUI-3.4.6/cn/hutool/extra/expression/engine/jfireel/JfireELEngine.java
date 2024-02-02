package cn.hutool.extra.expression.engine.jfireel;

import cn.hutool.extra.expression.ExpressionEngine;
import com.jfirer.jfireel.expression.Expression;
import java.util.Map;

public class JfireELEngine implements ExpressionEngine {
   public Object eval(String expression, Map<String, Object> context) {
      return Expression.parse(expression).calculate(context);
   }
}
