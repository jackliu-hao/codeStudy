package cn.hutool.bloomfilter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HashUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.BitSet;

public class BitSetBloomFilter implements BloomFilter {
   private static final long serialVersionUID = 1L;
   private final BitSet bitSet;
   private final int bitSetSize;
   private final int addedElements;
   private final int hashFunctionNumber;

   public BitSetBloomFilter(int c, int n, int k) {
      this.hashFunctionNumber = k;
      this.bitSetSize = (int)Math.ceil((double)(c * k));
      this.addedElements = n;
      this.bitSet = new BitSet(this.bitSetSize);
   }

   /** @deprecated */
   @Deprecated
   public void init(String path, String charsetName) throws IOException {
      this.init(path, CharsetUtil.charset(charsetName));
   }

   public void init(String path, Charset charset) throws IOException {
      BufferedReader reader = FileUtil.getReader(path, charset);

      try {
         while(true) {
            String line = reader.readLine();
            if (line == null) {
               return;
            }

            this.add(line);
         }
      } finally {
         IoUtil.close(reader);
      }
   }

   public boolean add(String str) {
      if (this.contains(str)) {
         return false;
      } else {
         int[] positions = createHashes(str, this.hashFunctionNumber);
         int[] var3 = positions;
         int var4 = positions.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int value = var3[var5];
            int position = Math.abs(value % this.bitSetSize);
            this.bitSet.set(position, true);
         }

         return true;
      }
   }

   public boolean contains(String str) {
      int[] positions = createHashes(str, this.hashFunctionNumber);
      int[] var3 = positions;
      int var4 = positions.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int i = var3[var5];
         int position = Math.abs(i % this.bitSetSize);
         if (!this.bitSet.get(position)) {
            return false;
         }
      }

      return true;
   }

   public double getFalsePositiveProbability() {
      return Math.pow(1.0 - Math.exp((double)(-this.hashFunctionNumber) * (double)this.addedElements / (double)this.bitSetSize), (double)this.hashFunctionNumber);
   }

   public static int[] createHashes(String str, int hashNumber) {
      int[] result = new int[hashNumber];

      for(int i = 0; i < hashNumber; ++i) {
         result[i] = hash(str, i);
      }

      return result;
   }

   public static int hash(String str, int k) {
      switch (k) {
         case 0:
            return HashUtil.rsHash(str);
         case 1:
            return HashUtil.jsHash(str);
         case 2:
            return HashUtil.elfHash(str);
         case 3:
            return HashUtil.bkdrHash(str);
         case 4:
            return HashUtil.apHash(str);
         case 5:
            return HashUtil.djbHash(str);
         case 6:
            return HashUtil.sdbmHash(str);
         case 7:
            return HashUtil.pjwHash(str);
         default:
            return 0;
      }
   }
}
