package cn.hutool.core.lang;

@FunctionalInterface
public interface Replacer<T> {
   T replace(T var1);
}
