package cn.hutool.cache.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CacheObjIterator<K, V> implements Iterator<CacheObj<K, V>>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Iterator<CacheObj<K, V>> iterator;
   private CacheObj<K, V> nextValue;

   CacheObjIterator(Iterator<CacheObj<K, V>> iterator) {
      this.iterator = iterator;
      this.nextValue();
   }

   public boolean hasNext() {
      return this.nextValue != null;
   }

   public CacheObj<K, V> next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         CacheObj<K, V> cachedObject = this.nextValue;
         this.nextValue();
         return cachedObject;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("Cache values Iterator is not support to modify.");
   }

   private void nextValue() {
      while(true) {
         if (this.iterator.hasNext()) {
            this.nextValue = (CacheObj)this.iterator.next();
            if (this.nextValue.isExpired()) {
               continue;
            }

            return;
         }

         this.nextValue = null;
         return;
      }
   }
}
