package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Map;

public class StackTraceElementConverter extends AbstractConverter<StackTraceElement> {
   private static final long serialVersionUID = 1L;

   protected StackTraceElement convertInternal(Object value) {
      if (value instanceof Map) {
         Map<?, ?> map = (Map)value;
         String declaringClass = MapUtil.getStr(map, "className");
         String methodName = MapUtil.getStr(map, "methodName");
         String fileName = MapUtil.getStr(map, "fileName");
         Integer lineNumber = MapUtil.getInt(map, "lineNumber");
         return new StackTraceElement(declaringClass, methodName, fileName, (Integer)ObjectUtil.defaultIfNull(lineNumber, (int)0));
      } else {
         return null;
      }
   }
}
