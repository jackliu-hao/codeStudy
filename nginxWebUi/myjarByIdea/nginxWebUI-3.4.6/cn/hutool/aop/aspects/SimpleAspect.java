package cn.hutool.aop.aspects;

import java.io.Serializable;
import java.lang.reflect.Method;

public class SimpleAspect implements Aspect, Serializable {
   private static final long serialVersionUID = 1L;

   public boolean before(Object target, Method method, Object[] args) {
      return true;
   }

   public boolean after(Object target, Method method, Object[] args, Object returnVal) {
      return true;
   }

   public boolean afterException(Object target, Method method, Object[] args, Throwable e) {
      return true;
   }
}
