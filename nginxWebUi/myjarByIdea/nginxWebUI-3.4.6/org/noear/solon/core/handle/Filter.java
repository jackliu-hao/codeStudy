package org.noear.solon.core.handle;

@FunctionalInterface
public interface Filter {
   void doFilter(Context ctx, FilterChain chain) throws Throwable;
}
