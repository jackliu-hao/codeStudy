package cn.hutool.core.lang;

@FunctionalInterface
public interface Editor<T> {
   T edit(T var1);
}
