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

   <T extends Annotation> T getAnnotation(Class<T> type);
}
