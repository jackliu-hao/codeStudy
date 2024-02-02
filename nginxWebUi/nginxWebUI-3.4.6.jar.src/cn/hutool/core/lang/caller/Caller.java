package cn.hutool.core.lang.caller;

public interface Caller {
  Class<?> getCaller();
  
  Class<?> getCallerCaller();
  
  Class<?> getCaller(int paramInt);
  
  boolean isCalledBy(Class<?> paramClass);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\caller\Caller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */