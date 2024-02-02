/*     */ package org.wildfly.common.rpc;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ final class IdentityIntMap<T>
/*     */   implements Cloneable
/*     */ {
/*     */   private int[] values;
/*     */   private Object[] keys;
/*     */   private int count;
/*     */   private int resizeCount;
/*     */   
/*     */   public IdentityIntMap(int initialCapacity, float loadFactor) {
/*  39 */     if (initialCapacity < 1) {
/*  40 */       throw new IllegalArgumentException("initialCapacity must be > 0");
/*     */     }
/*  42 */     if (loadFactor <= 0.0F || loadFactor >= 1.0F) {
/*  43 */       throw new IllegalArgumentException("loadFactor must be > 0.0 and < 1.0");
/*     */     }
/*  45 */     if (initialCapacity < 16) {
/*  46 */       initialCapacity = 16;
/*     */     } else {
/*     */       
/*  49 */       int c = Integer.highestOneBit(initialCapacity) - 1;
/*  50 */       initialCapacity = Integer.highestOneBit(initialCapacity + c);
/*     */     } 
/*  52 */     this.keys = new Object[initialCapacity];
/*  53 */     this.values = new int[initialCapacity];
/*  54 */     this.resizeCount = (int)(initialCapacity * loadFactor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityIntMap<T> clone() {
/*     */     try {
/*  65 */       IdentityIntMap<T> clone = (IdentityIntMap<T>)super.clone();
/*  66 */       clone.values = (int[])this.values.clone();
/*  67 */       clone.keys = (Object[])this.keys.clone();
/*  68 */       return clone;
/*  69 */     } catch (CloneNotSupportedException e) {
/*  70 */       throw new IllegalStateException();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityIntMap(float loadFactor) {
/*  80 */     this(64, loadFactor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityIntMap(int initialCapacity) {
/*  89 */     this(initialCapacity, 0.5F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdentityIntMap() {
/*  96 */     this(0.5F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int get(T key, int defVal) {
/* 107 */     Object[] keys = this.keys;
/* 108 */     int mask = keys.length - 1;
/* 109 */     int hc = System.identityHashCode(key) & mask;
/*     */     
/*     */     while (true) {
/* 112 */       Object v = keys[hc];
/* 113 */       if (v == key) {
/* 114 */         return this.values[hc];
/*     */       }
/* 116 */       if (v == null)
/*     */       {
/* 118 */         return defVal;
/*     */       }
/* 120 */       hc = hc + 1 & mask;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(T key, int value) {
/* 131 */     Object[] keys = this.keys;
/* 132 */     int mask = keys.length - 1;
/* 133 */     int[] values = this.values;
/*     */     
/* 135 */     int hc = System.identityHashCode(key) & mask; int idx;
/* 136 */     for (idx = hc;; idx = hc++ & mask) {
/* 137 */       Object v = keys[idx];
/* 138 */       if (v == null) {
/* 139 */         keys[idx] = key;
/* 140 */         values[idx] = value;
/* 141 */         if (++this.count > this.resizeCount) {
/* 142 */           resize();
/*     */         }
/*     */         return;
/*     */       } 
/* 146 */       if (v == key) {
/* 147 */         values[idx] = value;
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void resize() {
/* 154 */     Object[] oldKeys = this.keys;
/* 155 */     int oldsize = oldKeys.length;
/* 156 */     int[] oldValues = this.values;
/* 157 */     if (oldsize >= 1073741824) {
/* 158 */       throw new IllegalStateException("Table full");
/*     */     }
/* 160 */     int newsize = oldsize << 1;
/* 161 */     int mask = newsize - 1;
/* 162 */     Object[] newKeys = new Object[newsize];
/* 163 */     int[] newValues = new int[newsize];
/* 164 */     this.keys = newKeys;
/* 165 */     this.values = newValues;
/* 166 */     if ((this.resizeCount <<= 1) == 0) {
/* 167 */       this.resizeCount = Integer.MAX_VALUE;
/*     */     }
/* 169 */     for (int oi = 0; oi < oldsize; oi++) {
/* 170 */       Object key = oldKeys[oi];
/* 171 */       if (key != null) {
/* 172 */         int ni = System.identityHashCode(key) & mask;
/*     */         while (true) {
/* 174 */           Object v = newKeys[ni];
/* 175 */           if (v == null) {
/*     */             
/* 177 */             newKeys[ni] = key;
/* 178 */             newValues[ni] = oldValues[oi];
/*     */             break;
/*     */           } 
/* 181 */           ni = ni + 1 & mask;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void clear() {
/* 188 */     Arrays.fill(this.keys, (Object)null);
/* 189 */     this.count = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 198 */     StringBuilder builder = new StringBuilder();
/* 199 */     builder.append("Map length = ").append(this.keys.length).append(", count = ").append(this.count).append(", resize count = ").append(this.resizeCount).append('\n');
/* 200 */     for (int i = 0; i < this.keys.length; i++) {
/* 201 */       builder.append('[').append(i).append("] = ");
/* 202 */       if (this.keys[i] != null) {
/* 203 */         int hc = System.identityHashCode(this.keys[i]);
/* 204 */         builder.append("{ ").append(this.keys[i]).append(" (hash ").append(hc).append(", modulus ").append(hc % this.keys.length).append(") => ").append(this.values[i]).append(" }");
/*     */       } else {
/* 206 */         builder.append("(blank)");
/*     */       } 
/* 208 */       builder.append('\n');
/*     */     } 
/* 210 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\rpc\IdentityIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */