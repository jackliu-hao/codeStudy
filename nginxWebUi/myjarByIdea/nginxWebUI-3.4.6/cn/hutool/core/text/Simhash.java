package cn.hutool.core.text;

import cn.hutool.core.lang.hash.MurmurHash;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class Simhash {
   private final int bitNum;
   private final int fracCount;
   private final int fracBitNum;
   private final int hammingThresh;
   private final List<Map<String, List<Long>>> storage;
   private final StampedLock lock;

   public Simhash() {
      this(4, 3);
   }

   public Simhash(int fracCount, int hammingThresh) {
      this.bitNum = 64;
      this.lock = new StampedLock();
      this.fracCount = fracCount;
      this.fracBitNum = 64 / fracCount;
      this.hammingThresh = hammingThresh;
      this.storage = new ArrayList(fracCount);

      for(int i = 0; i < fracCount; ++i) {
         this.storage.add(new HashMap());
      }

   }

   public long hash(Collection<? extends CharSequence> segList) {
      this.getClass();
      int bitNum = 64;
      int[] weight = new int[bitNum];
      Iterator var6 = segList.iterator();

      while(var6.hasNext()) {
         CharSequence seg = (CharSequence)var6.next();
         long wordHash = MurmurHash.hash64(seg);

         for(int i = 0; i < bitNum; ++i) {
            int var10002;
            if ((wordHash >> i & 1L) == 1L) {
               var10002 = weight[i]++;
            } else {
               var10002 = weight[i]--;
            }
         }
      }

      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < bitNum; ++i) {
         sb.append(weight[i] > 0 ? 1 : 0);
      }

      return (new BigInteger(sb.toString(), 2)).longValue();
   }

   public boolean equals(Collection<? extends CharSequence> segList) {
      long simhash = this.hash(segList);
      List<String> fracList = this.splitSimhash(simhash);
      int hammingThresh = this.hammingThresh;
      long stamp = this.lock.readLock();

      try {
         for(int i = 0; i < this.fracCount; ++i) {
            String frac = (String)fracList.get(i);
            Map<String, List<Long>> fracMap = (Map)this.storage.get(i);
            if (fracMap.containsKey(frac)) {
               Iterator var11 = ((List)fracMap.get(frac)).iterator();

               while(var11.hasNext()) {
                  Long simhash2 = (Long)var11.next();
                  if (this.hamming(simhash, simhash2) < hammingThresh) {
                     boolean var13 = true;
                     return var13;
                  }
               }
            }
         }
      } finally {
         this.lock.unlockRead(stamp);
      }

      return false;
   }

   public void store(Long simhash) {
      int fracCount = this.fracCount;
      List<Map<String, List<Long>>> storage = this.storage;
      List<String> lFrac = this.splitSimhash(simhash);
      long stamp = this.lock.writeLock();

      try {
         for(int i = 0; i < fracCount; ++i) {
            String frac = (String)lFrac.get(i);
            Map<String, List<Long>> fracMap = (Map)storage.get(i);
            if (fracMap.containsKey(frac)) {
               ((List)fracMap.get(frac)).add(simhash);
            } else {
               List<Long> ls = new ArrayList();
               ls.add(simhash);
               fracMap.put(frac, ls);
            }
         }
      } finally {
         this.lock.unlockWrite(stamp);
      }

   }

   private int hamming(Long s1, Long s2) {
      this.getClass();
      int bitNum = 64;
      int dis = 0;

      for(int i = 0; i < bitNum; ++i) {
         if ((s1 >> i & 1L) != (s2 >> i & 1L)) {
            ++dis;
         }
      }

      return dis;
   }

   private List<String> splitSimhash(Long simhash) {
      this.getClass();
      int bitNum = 64;
      int fracBitNum = this.fracBitNum;
      List<String> ls = new ArrayList();
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < bitNum; ++i) {
         sb.append(simhash >> i & 1L);
         if ((i + 1) % fracBitNum == 0) {
            ls.add(sb.toString());
            sb.setLength(0);
         }
      }

      return ls;
   }
}
