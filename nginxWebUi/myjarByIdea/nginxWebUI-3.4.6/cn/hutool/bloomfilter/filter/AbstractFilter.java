package cn.hutool.bloomfilter.filter;

import cn.hutool.bloomfilter.BloomFilter;
import cn.hutool.bloomfilter.bitMap.BitMap;
import cn.hutool.bloomfilter.bitMap.IntMap;
import cn.hutool.bloomfilter.bitMap.LongMap;

public abstract class AbstractFilter implements BloomFilter {
   private static final long serialVersionUID = 1L;
   protected static int DEFAULT_MACHINE_NUM = 32;
   private BitMap bm;
   protected long size;

   public AbstractFilter(long maxValue, int machineNum) {
      this.bm = null;
      this.init(maxValue, machineNum);
   }

   public AbstractFilter(long maxValue) {
      this(maxValue, DEFAULT_MACHINE_NUM);
   }

   public void init(long maxValue, int machineNum) {
      this.size = maxValue;
      switch (machineNum) {
         case 32:
            this.bm = new IntMap((int)(this.size / (long)machineNum));
            break;
         case 64:
            this.bm = new LongMap((int)(this.size / (long)machineNum));
            break;
         default:
            throw new RuntimeException("Error Machine number!");
      }

   }

   public boolean contains(String str) {
      return this.bm.contains(Math.abs(this.hash(str)));
   }

   public boolean add(String str) {
      long hash = Math.abs(this.hash(str));
      if (this.bm.contains(hash)) {
         return false;
      } else {
         this.bm.add(hash);
         return true;
      }
   }

   public abstract long hash(String var1);
}
