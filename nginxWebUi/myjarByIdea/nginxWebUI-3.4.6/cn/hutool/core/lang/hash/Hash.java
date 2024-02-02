package cn.hutool.core.lang.hash;

@FunctionalInterface
public interface Hash<T> {
   Number hash(T var1);
}
