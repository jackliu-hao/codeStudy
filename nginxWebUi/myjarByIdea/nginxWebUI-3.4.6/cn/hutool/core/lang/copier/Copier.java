package cn.hutool.core.lang.copier;

@FunctionalInterface
public interface Copier<T> {
   T copy();
}
