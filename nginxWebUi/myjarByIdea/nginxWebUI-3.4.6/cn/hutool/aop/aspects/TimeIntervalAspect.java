package cn.hutool.aop.aspects;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import java.lang.reflect.Method;

public class TimeIntervalAspect extends SimpleAspect {
   private static final long serialVersionUID = 1L;
   private final TimeInterval interval = new TimeInterval();

   public boolean before(Object target, Method method, Object[] args) {
      this.interval.start();
      return true;
   }

   public boolean after(Object target, Method method, Object[] args, Object returnVal) {
      Console.log("Method [{}.{}] execute spend [{}]ms return value [{}]", target.getClass().getName(), method.getName(), this.interval.intervalMs(), returnVal);
      return true;
   }
}
