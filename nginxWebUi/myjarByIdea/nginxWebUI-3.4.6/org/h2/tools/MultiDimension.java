package org.h2.tools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import org.h2.util.StringUtils;

public class MultiDimension implements Comparator<long[]> {
   private static final MultiDimension INSTANCE = new MultiDimension();

   protected MultiDimension() {
   }

   public static MultiDimension getInstance() {
      return INSTANCE;
   }

   public int normalize(int var1, double var2, double var4, double var6) {
      if (!(var2 < var4) && !(var2 > var6)) {
         double var8 = (var2 - var4) / (var6 - var4);
         return (int)(var8 * (double)this.getMaxValue(var1));
      } else {
         throw new IllegalArgumentException(var4 + "<" + var2 + "<" + var6);
      }
   }

   public int getMaxValue(int var1) {
      if (var1 >= 2 && var1 <= 32) {
         int var2 = getBitsPerValue(var1);
         return (int)((1L << var2) - 1L);
      } else {
         throw new IllegalArgumentException(Integer.toString(var1));
      }
   }

   private static int getBitsPerValue(int var0) {
      return Math.min(31, 64 / var0);
   }

   public long interleave(int... var1) {
      int var2 = var1.length;
      long var3 = (long)this.getMaxValue(var2);
      int var5 = getBitsPerValue(var2);
      long var6 = 0L;

      for(int var8 = 0; var8 < var2; ++var8) {
         long var9 = (long)var1[var8];
         if (var9 < 0L || var9 > var3) {
            throw new IllegalArgumentException("0<" + var9 + "<" + var3);
         }

         for(int var11 = 0; var11 < var5; ++var11) {
            var6 |= (var9 & 1L << var11) << var8 + (var2 - 1) * var11;
         }
      }

      return var6;
   }

   public long interleave(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("0<" + var1);
      } else if (var2 < 0) {
         throw new IllegalArgumentException("0<" + var2);
      } else {
         long var3 = 0L;

         for(int var5 = 0; var5 < 32; ++var5) {
            var3 |= ((long)var1 & 1L << var5) << var5;
            var3 |= ((long)var2 & 1L << var5) << var5 + 1;
         }

         return var3;
      }
   }

   public int deinterleave(int var1, long var2, int var4) {
      int var5 = getBitsPerValue(var1);
      int var6 = 0;

      for(int var7 = 0; var7 < var5; ++var7) {
         var6 = (int)((long)var6 | var2 >> var4 + (var1 - 1) * var7 & 1L << var7);
      }

      return var6;
   }

   public String generatePreparedQuery(String var1, String var2, String[] var3) {
      StringBuilder var4 = new StringBuilder("SELECT D.* FROM ");
      StringUtils.quoteIdentifier(var4, var1).append(" D, TABLE(_FROM_ BIGINT=?, _TO_ BIGINT=?) WHERE ");
      StringUtils.quoteIdentifier(var4, var2).append(" BETWEEN _FROM_ AND _TO_");
      String[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         var4.append(" AND ");
         StringUtils.quoteIdentifier(var4, var8).append("+1 BETWEEN ?+1 AND ?+1");
      }

      return var4.toString();
   }

   public ResultSet getResult(PreparedStatement var1, int[] var2, int[] var3) throws SQLException {
      long[][] var4 = this.getMortonRanges(var2, var3);
      int var5 = var4.length;
      Long[] var6 = new Long[var5];
      Long[] var7 = new Long[var5];

      int var8;
      for(var8 = 0; var8 < var5; ++var8) {
         var6[var8] = var4[var8][0];
         var7[var8] = var4[var8][1];
      }

      var1.setObject(1, var6);
      var1.setObject(2, var7);
      var5 = var2.length;
      var8 = 0;

      for(int var9 = 3; var8 < var5; ++var8) {
         var1.setInt(var9++, var2[var8]);
         var1.setInt(var9++, var3[var8]);
      }

      return var1.executeQuery();
   }

   private long[][] getMortonRanges(int[] var1, int[] var2) {
      int var3 = var1.length;
      if (var2.length != var3) {
         throw new IllegalArgumentException(var3 + "=" + var2.length);
      } else {
         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            if (var1[var4] > var2[var4]) {
               int var5 = var1[var4];
               var1[var4] = var2[var4];
               var2[var4] = var5;
            }
         }

         var4 = getSize(var1, var2, var3);
         ArrayList var6 = new ArrayList();
         this.addMortonRanges(var6, var1, var2, var3, 0);
         this.combineEntries(var6, var4);
         return (long[][])var6.toArray(new long[0][]);
      }
   }

   private static int getSize(int[] var0, int[] var1, int var2) {
      int var3 = 1;

      for(int var4 = 0; var4 < var2; ++var4) {
         int var5 = var1[var4] - var0[var4];
         var3 *= var5 + 1;
      }

      return var3;
   }

   private void combineEntries(ArrayList<long[]> var1, int var2) {
      var1.sort(this);

      for(int var3 = 10; var3 < var2; var3 += var3 / 2) {
         int var4;
         long[] var6;
         for(var4 = 0; var4 < var1.size() - 1; ++var4) {
            long[] var5 = (long[])var1.get(var4);
            var6 = (long[])var1.get(var4 + 1);
            if (var5[1] + (long)var3 >= var6[0]) {
               var5[1] = var6[1];
               var1.remove(var4 + 1);
               --var4;
            }
         }

         var4 = 0;

         for(Iterator var7 = var1.iterator(); var7.hasNext(); var4 = (int)((long)var4 + var6[1] - var6[0] + 1L)) {
            var6 = (long[])var7.next();
         }

         if (var4 > 2 * var2 || var1.size() < 100) {
            break;
         }
      }

   }

   public int compare(long[] var1, long[] var2) {
      return var1[0] > var2[0] ? 1 : -1;
   }

   private void addMortonRanges(ArrayList<long[]> var1, int[] var2, int[] var3, int var4, int var5) {
      if (var5 > 100) {
         throw new IllegalArgumentException(Integer.toString(var5));
      } else {
         int var6 = 0;
         int var7 = 0;
         long var8 = 1L;

         for(int var10 = 0; var10 < var4; ++var10) {
            int var11 = var3[var10] - var2[var10];
            if (var11 < 0) {
               throw new IllegalArgumentException(Integer.toString(var11));
            }

            var8 *= (long)(var11 + 1);
            if (var8 < 0L) {
               throw new IllegalArgumentException(Long.toString(var8));
            }

            if (var11 > var7) {
               var7 = var11;
               var6 = var10;
            }
         }

         long var18 = this.interleave(var2);
         long var12 = this.interleave(var3);
         if (var12 < var18) {
            throw new IllegalArgumentException(var12 + "<" + var18);
         } else {
            long var14 = var12 - var18 + 1L;
            if (var14 == var8) {
               long[] var16 = new long[]{var18, var12};
               var1.add(var16);
            } else {
               int var19 = findMiddle(var2[var6], var3[var6]);
               int var17 = var3[var6];
               var3[var6] = var19;
               this.addMortonRanges(var1, var2, var3, var4, var5 + 1);
               var3[var6] = var17;
               var17 = var2[var6];
               var2[var6] = var19 + 1;
               this.addMortonRanges(var1, var2, var3, var4, var5 + 1);
               var2[var6] = var17;
            }

         }
      }
   }

   private static int roundUp(int var0, int var1) {
      return var0 + var1 - 1 & -var1;
   }

   private static int findMiddle(int var0, int var1) {
      int var2 = var1 - var0 - 1;
      if (var2 == 0) {
         return var0;
      } else if (var2 == 1) {
         return var0 + 1;
      } else {
         int var3;
         for(var3 = 0; 1 << var3 < var2; ++var3) {
         }

         --var3;
         int var4 = roundUp(var0 + 2, 1 << var3) - 1;
         if (var4 > var0 && var4 < var1) {
            return var4;
         } else {
            throw new IllegalArgumentException(var0 + "<" + var4 + "<" + var1);
         }
      }
   }
}
