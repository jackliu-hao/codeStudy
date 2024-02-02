package cn.hutool.core.lang.hash;

@FunctionalInterface
public interface Hash64<T> extends Hash<T> {
   long hash64(T var1);

   default Number hash(T t) {
      return this.hash64(t);
   }
}
