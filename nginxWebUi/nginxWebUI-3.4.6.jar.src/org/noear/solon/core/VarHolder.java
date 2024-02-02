package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

public interface VarHolder {
  AopContext context();
  
  ParameterizedType getGenericType();
  
  boolean isField();
  
  Class<?> getType();
  
  Annotation[] getAnnoS();
  
  void setValue(Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\VarHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */