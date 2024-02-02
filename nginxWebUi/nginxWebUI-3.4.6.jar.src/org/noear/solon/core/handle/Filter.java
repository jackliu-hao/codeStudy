package org.noear.solon.core.handle;

@FunctionalInterface
public interface Filter {
  void doFilter(Context paramContext, FilterChain paramFilterChain) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */