package org.noear.solon.core;

@FunctionalInterface
public interface BeanInjector<T extends java.lang.annotation.Annotation> {
  void doInject(VarHolder paramVarHolder, T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\BeanInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */