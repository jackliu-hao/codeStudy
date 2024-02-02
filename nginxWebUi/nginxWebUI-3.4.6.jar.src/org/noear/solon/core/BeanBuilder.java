package org.noear.solon.core;

@FunctionalInterface
public interface BeanBuilder<T extends java.lang.annotation.Annotation> {
  void doBuild(Class<?> paramClass, BeanWrap paramBeanWrap, T paramT) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\BeanBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */