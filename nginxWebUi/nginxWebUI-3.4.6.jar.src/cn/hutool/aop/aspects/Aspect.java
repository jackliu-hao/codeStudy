package cn.hutool.aop.aspects;

import java.lang.reflect.Method;

public interface Aspect {
  boolean before(Object paramObject, Method paramMethod, Object[] paramArrayOfObject);
  
  boolean after(Object paramObject1, Method paramMethod, Object[] paramArrayOfObject, Object paramObject2);
  
  boolean afterException(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\aop\aspects\Aspect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */