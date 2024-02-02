package cn.hutool.extra.expression;

import cn.hutool.extra.expression.engine.ExpressionFactory;
import java.util.Map;

public class ExpressionUtil {
   public static ExpressionEngine getEngine() {
      return ExpressionFactory.get();
   }

   public static Object eval(String expression, Map<String, Object> context) {
      return getEngine().eval(expression, context);
   }
}
