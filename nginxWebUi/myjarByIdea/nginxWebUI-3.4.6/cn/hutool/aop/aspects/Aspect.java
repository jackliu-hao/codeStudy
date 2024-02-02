package cn.hutool.aop.aspects;

import java.lang.reflect.Method;

public interface Aspect {
   boolean before(Object var1, Method var2, Object[] var3);

   boolean after(Object var1, Method var2, Object[] var3, Object var4);

   boolean afterException(Object var1, Method var2, Object[] var3, Throwable var4);
}
