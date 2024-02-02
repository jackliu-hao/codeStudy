package cn.hutool.bloomfilter.bitMap;

public interface BitMap {
   int MACHINE32 = 32;
   int MACHINE64 = 64;

   void add(long var1);

   boolean contains(long var1);

   void remove(long var1);
}
