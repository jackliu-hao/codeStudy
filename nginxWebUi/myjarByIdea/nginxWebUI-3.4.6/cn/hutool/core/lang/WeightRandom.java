package cn.hutool.core.lang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class WeightRandom<T> implements Serializable {
   private static final long serialVersionUID = -8244697995702786499L;
   private final TreeMap<Double, T> weightMap;

   public static <T> WeightRandom<T> create() {
      return new WeightRandom();
   }

   public WeightRandom() {
      this.weightMap = new TreeMap();
   }

   public WeightRandom(WeightObj<T> weightObj) {
      this();
      if (null != weightObj) {
         this.add(weightObj);
      }

   }

   public WeightRandom(Iterable<WeightObj<T>> weightObjs) {
      this();
      if (CollUtil.isNotEmpty(weightObjs)) {
         Iterator var2 = weightObjs.iterator();

         while(var2.hasNext()) {
            WeightObj<T> weightObj = (WeightObj)var2.next();
            this.add(weightObj);
         }
      }

   }

   public WeightRandom(WeightObj<T>[] weightObjs) {
      this();
      WeightObj[] var2 = weightObjs;
      int var3 = weightObjs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WeightObj<T> weightObj = var2[var4];
         this.add(weightObj);
      }

   }

   public WeightRandom<T> add(T obj, double weight) {
      return this.add(new WeightObj(obj, weight));
   }

   public WeightRandom<T> add(WeightObj<T> weightObj) {
      if (null != weightObj) {
         double weight = weightObj.getWeight();
         if (weightObj.getWeight() > 0.0) {
            double lastWeight = this.weightMap.size() == 0 ? 0.0 : (Double)this.weightMap.lastKey();
            this.weightMap.put(weight + lastWeight, weightObj.getObj());
         }
      }

      return this;
   }

   public WeightRandom<T> clear() {
      if (null != this.weightMap) {
         this.weightMap.clear();
      }

      return this;
   }

   public T next() {
      if (MapUtil.isEmpty(this.weightMap)) {
         return null;
      } else {
         Random random = RandomUtil.getRandom();
         double randomWeight = (Double)this.weightMap.lastKey() * random.nextDouble();
         SortedMap<Double, T> tailMap = this.weightMap.tailMap(randomWeight, false);
         return this.weightMap.get(tailMap.firstKey());
      }
   }

   public static class WeightObj<T> {
      private T obj;
      private final double weight;

      public WeightObj(T obj, double weight) {
         this.obj = obj;
         this.weight = weight;
      }

      public T getObj() {
         return this.obj;
      }

      public void setObj(T obj) {
         this.obj = obj;
      }

      public double getWeight() {
         return this.weight;
      }

      public int hashCode() {
         int prime = true;
         int result = 1;
         result = 31 * result + (this.obj == null ? 0 : this.obj.hashCode());
         long temp = Double.doubleToLongBits(this.weight);
         result = 31 * result + (int)(temp ^ temp >>> 32);
         return result;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            WeightObj<?> other = (WeightObj)obj;
            if (this.obj == null) {
               if (other.obj != null) {
                  return false;
               }
            } else if (!this.obj.equals(other.obj)) {
               return false;
            }

            return Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(other.weight);
         }
      }
   }
}
