package org.noear.solon.core.aspect;

@FunctionalInterface
public interface Interceptor {
  Object doIntercept(Invocation paramInvocation) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\aspect\Interceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */