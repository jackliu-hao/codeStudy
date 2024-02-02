package cn.hutool.core.lang.caller;

public interface Caller {
   Class<?> getCaller();

   Class<?> getCallerCaller();

   Class<?> getCaller(int var1);

   boolean isCalledBy(Class<?> var1);
}
