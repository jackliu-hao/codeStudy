package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

public interface VarHolder {
   AopContext context();

   ParameterizedType getGenericType();

   boolean isField();

   Class<?> getType();

   Annotation[] getAnnoS();

   void setValue(Object val);
}
