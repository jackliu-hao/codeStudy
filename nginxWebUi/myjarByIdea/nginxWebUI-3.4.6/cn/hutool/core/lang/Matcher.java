package cn.hutool.core.lang;

@FunctionalInterface
public interface Matcher<T> {
   boolean match(T var1);
}
