package cn.hutool.core.math;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arrangement implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String[] datas;

   public Arrangement(String[] datas) {
      this.datas = datas;
   }

   public static long count(int n) {
      return count(n, n);
   }

   public static long count(int n, int m) {
      if (n == m) {
         return NumberUtil.factorial((long)n);
      } else {
         return n > m ? NumberUtil.factorial((long)n, (long)(n - m)) : 0L;
      }
   }

   public static long countAll(int n) {
      long total = 0L;

      for(int i = 1; i <= n; ++i) {
         total += count(n, i);
      }

      return total;
   }

   public List<String[]> select() {
      return this.select(this.datas.length);
   }

   public List<String[]> select(int m) {
      List<String[]> result = new ArrayList((int)count(this.datas.length, m));
      this.select(this.datas, new String[m], 0, result);
      return result;
   }

   public List<String[]> selectAll() {
      List<String[]> result = new ArrayList((int)countAll(this.datas.length));

      for(int i = 1; i <= this.datas.length; ++i) {
         result.addAll(this.select(i));
      }

      return result;
   }

   private void select(String[] datas, String[] resultList, int resultIndex, List<String[]> result) {
      if (resultIndex >= resultList.length) {
         if (!result.contains(resultList)) {
            result.add(Arrays.copyOf(resultList, resultList.length));
         }

      } else {
         for(int i = 0; i < datas.length; ++i) {
            resultList[resultIndex] = datas[i];
            this.select((String[])ArrayUtil.remove(datas, i), resultList, resultIndex + 1, result);
         }

      }
   }
}
