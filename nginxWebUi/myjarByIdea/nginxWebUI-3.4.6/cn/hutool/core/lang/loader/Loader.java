package cn.hutool.core.lang.loader;

@FunctionalInterface
public interface Loader<T> {
   T get();
}
