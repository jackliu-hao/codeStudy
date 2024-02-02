package org.wildfly.common.rpc;

import java.util.Arrays;

final class IdentityIntMap<T> implements Cloneable {
   private int[] values;
   private Object[] keys;
   private int count;
   private int resizeCount;

   public IdentityIntMap(int initialCapacity, float loadFactor) {
      if (initialCapacity < 1) {
         throw new IllegalArgumentException("initialCapacity must be > 0");
      } else if (!(loadFactor <= 0.0F) && !(loadFactor >= 1.0F)) {
         if (initialCapacity < 16) {
            initialCapacity = 16;
         } else {
            int c = Integer.highestOneBit(initialCapacity) - 1;
            initialCapacity = Integer.highestOneBit(initialCapacity + c);
         }

         this.keys = new Object[initialCapacity];
         this.values = new int[initialCapacity];
         this.resizeCount = (int)((double)initialCapacity * (double)loadFactor);
      } else {
         throw new IllegalArgumentException("loadFactor must be > 0.0 and < 1.0");
      }
   }

   public IdentityIntMap<T> clone() {
      try {
         IdentityIntMap<T> clone = (IdentityIntMap)super.clone();
         clone.values = (int[])this.values.clone();
         clone.keys = (Object[])this.keys.clone();
         return clone;
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException();
      }
   }

   public IdentityIntMap(float loadFactor) {
      this(64, loadFactor);
   }

   public IdentityIntMap(int initialCapacity) {
      this(initialCapacity, 0.5F);
   }

   public IdentityIntMap() {
      this(0.5F);
   }

   public int get(T key, int defVal) {
      Object[] keys = this.keys;
      int mask = keys.length - 1;
      int hc = System.identityHashCode(key) & mask;

      while(true) {
         Object v = keys[hc];
         if (v == key) {
            return this.values[hc];
         }

         if (v == null) {
            return defVal;
         }

         hc = hc + 1 & mask;
      }
   }

   public void put(T key, int value) {
      Object[] keys = this.keys;
      int mask = keys.length - 1;
      int[] values = this.values;
      int hc = System.identityHashCode(key) & mask;
      int idx = hc;

      while(true) {
         Object v = keys[idx];
         if (v == null) {
            keys[idx] = key;
            values[idx] = value;
            if (++this.count > this.resizeCount) {
               this.resize();
            }

            return;
         }

         if (v == key) {
            values[idx] = value;
            return;
         }

         idx = hc++ & mask;
      }
   }

   private void resize() {
      Object[] oldKeys = this.keys;
      int oldsize = oldKeys.length;
      int[] oldValues = this.values;
      if (oldsize >= 1073741824) {
         throw new IllegalStateException("Table full");
      } else {
         int newsize = oldsize << 1;
         int mask = newsize - 1;
         Object[] newKeys = new Object[newsize];
         int[] newValues = new int[newsize];
         this.keys = newKeys;
         this.values = newValues;
         if ((this.resizeCount <<= 1) == 0) {
            this.resizeCount = Integer.MAX_VALUE;
         }

         for(int oi = 0; oi < oldsize; ++oi) {
            Object key = oldKeys[oi];
            if (key != null) {
               int ni = System.identityHashCode(key) & mask;

               while(true) {
                  Object v = newKeys[ni];
                  if (v == null) {
                     newKeys[ni] = key;
                     newValues[ni] = oldValues[oi];
                     break;
                  }

                  ni = ni + 1 & mask;
               }
            }
         }

      }
   }

   public void clear() {
      Arrays.fill(this.keys, (Object)null);
      this.count = 0;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Map length = ").append(this.keys.length).append(", count = ").append(this.count).append(", resize count = ").append(this.resizeCount).append('\n');

      for(int i = 0; i < this.keys.length; ++i) {
         builder.append('[').append(i).append("] = ");
         if (this.keys[i] != null) {
            int hc = System.identityHashCode(this.keys[i]);
            builder.append("{ ").append(this.keys[i]).append(" (hash ").append(hc).append(", modulus ").append(hc % this.keys.length).append(") => ").append(this.values[i]).append(" }");
         } else {
            builder.append("(blank)");
         }

         builder.append('\n');
      }

      return builder.toString();
   }
}
