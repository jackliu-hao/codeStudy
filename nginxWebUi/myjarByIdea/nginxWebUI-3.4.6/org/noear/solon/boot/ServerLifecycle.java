package org.noear.solon.boot;

public interface ServerLifecycle {
   void start(String host, int port) throws Throwable;

   void stop() throws Throwable;
}
