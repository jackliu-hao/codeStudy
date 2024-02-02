package org.noear.solon.core.handle;

public interface FilterChain {
   void doFilter(Context ctx) throws Throwable;
}
