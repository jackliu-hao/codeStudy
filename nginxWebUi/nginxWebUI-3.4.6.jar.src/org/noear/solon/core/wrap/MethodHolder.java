package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.noear.solon.core.aspect.InterceptorEntity;

public interface MethodHolder {
  Method getMethod();
  
  ParamWrap[] getParamWraps();
  
  Class<?> getReturnType();
  
  Annotation[] getAnnotations();
  
  List<InterceptorEntity> getArounds();
  
  <T extends Annotation> T getAnnotation(Class<T> paramClass);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\MethodHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */