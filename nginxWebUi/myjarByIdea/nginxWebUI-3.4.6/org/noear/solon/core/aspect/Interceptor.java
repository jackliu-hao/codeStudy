package org.noear.solon.core.aspect;

@FunctionalInterface
public interface Interceptor {
   Object doIntercept(Invocation inv) throws Throwable;
}
