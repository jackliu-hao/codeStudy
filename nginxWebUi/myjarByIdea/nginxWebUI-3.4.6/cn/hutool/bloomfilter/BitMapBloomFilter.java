package cn.hutool.bloomfilter;

import cn.hutool.bloomfilter.filter.DefaultFilter;
import cn.hutool.bloomfilter.filter.ELFFilter;
import cn.hutool.bloomfilter.filter.JSFilter;
import cn.hutool.bloomfilter.filter.PJWFilter;
import cn.hutool.bloomfilter.filter.SDBMFilter;
import cn.hutool.core.util.NumberUtil;

public class BitMapBloomFilter implements BloomFilter {
   private static final long serialVersionUID = 1L;
   private BloomFilter[] filters;

   public BitMapBloomFilter(int m) {
      long mNum = NumberUtil.div(String.valueOf(m), String.valueOf(5)).longValue();
      long size = mNum * 1024L * 1024L * 8L;
      this.filters = new BloomFilter[]{new DefaultFilter(size), new ELFFilter(size), new JSFilter(size), new PJWFilter(size), new SDBMFilter(size)};
   }

   public BitMapBloomFilter(int m, BloomFilter... filters) {
      this(m);
      this.filters = filters;
   }

   public boolean add(String str) {
      boolean flag = false;
      BloomFilter[] var3 = this.filters;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BloomFilter filter = var3[var5];
         flag |= filter.add(str);
      }

      return flag;
   }

   public boolean contains(String str) {
      BloomFilter[] var2 = this.filters;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BloomFilter filter = var2[var4];
         if (!filter.contains(str)) {
            return false;
         }
      }

      return true;
   }
}
