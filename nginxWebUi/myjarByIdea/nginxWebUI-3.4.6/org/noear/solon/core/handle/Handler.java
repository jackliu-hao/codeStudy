package org.noear.solon.core.handle;

@FunctionalInterface
public interface Handler {
   void handle(Context ctx) throws Throwable;
}
