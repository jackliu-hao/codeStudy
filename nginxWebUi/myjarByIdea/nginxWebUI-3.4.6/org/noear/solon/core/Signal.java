package org.noear.solon.core;

public interface Signal {
   String name();

   int port();

   String protocol();

   SignalType type();
}
