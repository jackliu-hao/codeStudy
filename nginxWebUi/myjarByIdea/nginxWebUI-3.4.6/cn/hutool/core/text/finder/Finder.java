package cn.hutool.core.text.finder;

public interface Finder {
   int INDEX_NOT_FOUND = -1;

   int start(int var1);

   int end(int var1);

   default Finder reset() {
      return this;
   }
}
