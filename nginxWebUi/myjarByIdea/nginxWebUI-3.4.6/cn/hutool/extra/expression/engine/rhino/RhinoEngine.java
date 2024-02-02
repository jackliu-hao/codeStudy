package cn.hutool.extra.expression.engine.rhino;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoEngine implements ExpressionEngine {
   public Object eval(String expression, Map<String, Object> context) {
      Context ctx = Context.enter();
      Scriptable scope = ctx.initStandardObjects();
      if (MapUtil.isNotEmpty(context)) {
         context.forEach((key, value) -> {
            ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope));
         });
      }

      Object result = ctx.evaluateString(scope, expression, "rhino.js", 1, (Object)null);
      Context.exit();
      return result;
   }
}
