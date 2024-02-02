package cn.hutool.core.lang.hash;

@FunctionalInterface
public interface Hash32<T> extends Hash<T> {
   int hash32(T var1);

   default Number hash(T t) {
      return this.hash32(t);
   }
}
