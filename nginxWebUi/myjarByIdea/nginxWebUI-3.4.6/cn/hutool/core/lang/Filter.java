package cn.hutool.core.lang;

@FunctionalInterface
public interface Filter<T> {
   boolean accept(T var1);
}
