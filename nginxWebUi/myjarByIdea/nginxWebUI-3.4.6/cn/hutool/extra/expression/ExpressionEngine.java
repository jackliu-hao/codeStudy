package cn.hutool.extra.expression;

import java.util.Map;

public interface ExpressionEngine {
   Object eval(String var1, Map<String, Object> var2);
}
