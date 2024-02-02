/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Random;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WeightRandom<T>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8244697995702786499L;
/*     */   private final TreeMap<Double, T> weightMap;
/*     */   
/*     */   public static <T> WeightRandom<T> create() {
/*  44 */     return new WeightRandom<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom() {
/*  52 */     this.weightMap = new TreeMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom(WeightObj<T> weightObj) {
/*  62 */     this();
/*  63 */     if (null != weightObj) {
/*  64 */       add(weightObj);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom(Iterable<WeightObj<T>> weightObjs) {
/*  74 */     this();
/*  75 */     if (CollUtil.isNotEmpty(weightObjs)) {
/*  76 */       for (WeightObj<T> weightObj : weightObjs) {
/*  77 */         add(weightObj);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom(WeightObj<T>[] weightObjs) {
/*  88 */     this();
/*  89 */     for (WeightObj<T> weightObj : weightObjs) {
/*  90 */       add(weightObj);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom<T> add(T obj, double weight) {
/* 103 */     return add(new WeightObj<>(obj, weight));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom<T> add(WeightObj<T> weightObj) {
/* 113 */     if (null != weightObj) {
/* 114 */       double weight = weightObj.getWeight();
/* 115 */       if (weightObj.getWeight() > 0.0D) {
/* 116 */         double lastWeight = (this.weightMap.size() == 0) ? 0.0D : ((Double)this.weightMap.lastKey()).doubleValue();
/* 117 */         this.weightMap.put(Double.valueOf(weight + lastWeight), weightObj.getObj());
/*     */       } 
/*     */     } 
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightRandom<T> clear() {
/* 129 */     if (null != this.weightMap) {
/* 130 */       this.weightMap.clear();
/*     */     }
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T next() {
/* 141 */     if (MapUtil.isEmpty(this.weightMap)) {
/* 142 */       return null;
/*     */     }
/* 144 */     Random random = RandomUtil.getRandom();
/* 145 */     double randomWeight = ((Double)this.weightMap.lastKey()).doubleValue() * random.nextDouble();
/* 146 */     SortedMap<Double, T> tailMap = this.weightMap.tailMap(Double.valueOf(randomWeight), false);
/* 147 */     return this.weightMap.get(tailMap.firstKey());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class WeightObj<T>
/*     */   {
/*     */     private T obj;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final double weight;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WeightObj(T obj, double weight) {
/* 170 */       this.obj = obj;
/* 171 */       this.weight = weight;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public T getObj() {
/* 180 */       return this.obj;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setObj(T obj) {
/* 189 */       this.obj = obj;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getWeight() {
/* 198 */       return this.weight;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 203 */       int prime = 31;
/* 204 */       int result = 1;
/* 205 */       result = 31 * result + ((this.obj == null) ? 0 : this.obj.hashCode());
/*     */       
/* 207 */       long temp = Double.doubleToLongBits(this.weight);
/* 208 */       result = 31 * result + (int)(temp ^ temp >>> 32L);
/* 209 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 214 */       if (this == obj) {
/* 215 */         return true;
/*     */       }
/* 217 */       if (obj == null) {
/* 218 */         return false;
/*     */       }
/* 220 */       if (getClass() != obj.getClass()) {
/* 221 */         return false;
/*     */       }
/* 223 */       WeightObj<?> other = (WeightObj)obj;
/* 224 */       if (this.obj == null) {
/* 225 */         if (other.obj != null) {
/* 226 */           return false;
/*     */         }
/* 228 */       } else if (!this.obj.equals(other.obj)) {
/* 229 */         return false;
/*     */       } 
/* 231 */       return (Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(other.weight));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\WeightRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */