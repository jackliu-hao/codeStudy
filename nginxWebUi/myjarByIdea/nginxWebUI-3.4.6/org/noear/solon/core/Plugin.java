package org.noear.solon.core;

@FunctionalInterface
public interface Plugin {
   void start(AopContext context);

   default void prestop() throws Throwable {
   }

   default void stop() throws Throwable {
   }
}
