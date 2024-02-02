package org.noear.solon.core;

import java.lang.reflect.Method;

@FunctionalInterface
public interface BeanExtractor<T extends java.lang.annotation.Annotation> {
  void doExtract(BeanWrap paramBeanWrap, Method paramMethod, T paramT) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\BeanExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */