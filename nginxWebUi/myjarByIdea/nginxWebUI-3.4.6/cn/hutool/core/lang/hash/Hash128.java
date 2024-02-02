package cn.hutool.core.lang.hash;

@FunctionalInterface
public interface Hash128<T> extends Hash<T> {
   Number128 hash128(T var1);

   default Number hash(T t) {
      return this.hash128(t);
   }
}
