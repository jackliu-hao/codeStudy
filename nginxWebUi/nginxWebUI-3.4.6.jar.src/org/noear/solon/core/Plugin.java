package org.noear.solon.core;

@FunctionalInterface
public interface Plugin {
  void start(AopContext paramAopContext);
  
  default void prestop() throws Throwable {}
  
  default void stop() throws Throwable {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\Plugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */