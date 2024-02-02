package io.undertow.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class SubstringMap<V> {
   private static final int ALL_BUT_LAST_BIT = -2;
   private volatile Object[] table = new Object[16];
   private int size;

   public SubstringMatch<V> get(String key, int length) {
      return this.get(key, length, false);
   }

   public SubstringMatch<V> get(String key) {
      return this.get(key, key.length(), false);
   }

   private SubstringMatch<V> get(String key, int length, boolean exact) {
      if (key.length() < length) {
         throw new IllegalArgumentException();
      } else {
         Object[] table = this.table;
         int hash = hash(key, length);
         int pos = this.tablePos(table, hash);
         int start = pos;

         do {
            if (table[pos] == null) {
               return null;
            }

            if (exact) {
               if (table[pos].equals(key)) {
                  return (SubstringMatch)table[pos + 1];
               }
            } else if (this.doEquals((String)table[pos], key, length)) {
               return (SubstringMatch)table[pos + 1];
            }

            pos += 2;
            if (pos >= table.length) {
               pos = 0;
            }
         } while(pos != start);

         return null;
      }
   }

   private int tablePos(Object[] table, int hash) {
      return hash & table.length - 1 & -2;
   }

   private boolean doEquals(String s1, String s2, int length) {
      if (s1.length() == length && s2.length() >= length) {
         for(int i = 0; i < length; ++i) {
            if (s1.charAt(i) != s2.charAt(i)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public synchronized void put(String key, V value) {
      if (key == null) {
         throw new NullPointerException();
      } else {
         Object[] newTable;
         if ((double)this.table.length / (double)this.size < 4.0 && this.table.length != Integer.MAX_VALUE) {
            newTable = new Object[this.table.length << 1];

            for(int i = 0; i < this.table.length; i += 2) {
               if (this.table[i] != null) {
                  this.doPut(newTable, (String)this.table[i], this.table[i + 1]);
               }
            }
         } else {
            newTable = new Object[this.table.length];
            System.arraycopy(this.table, 0, newTable, 0, this.table.length);
         }

         this.doPut(newTable, key, new SubstringMatch(key, value));
         this.table = newTable;
         ++this.size;
      }
   }

   public synchronized V remove(String key) {
      if (key == null) {
         throw new NullPointerException();
      } else {
         V value = null;
         Object[] newTable = new Object[this.table.length];

         for(int i = 0; i < this.table.length; i += 2) {
            if (this.table[i] != null && !this.table[i].equals(key)) {
               this.doPut(newTable, (String)this.table[i], this.table[i + 1]);
            } else if (this.table[i] != null) {
               value = this.table[i + 1];
               --this.size;
            }
         }

         this.table = newTable;
         if (value == null) {
            return null;
         } else {
            return ((SubstringMatch)value).getValue();
         }
      }
   }

   private void doPut(Object[] newTable, String key, Object value) {
      int hash = hash(key, key.length());
      int pos = this.tablePos(newTable, hash);

      while(newTable[pos] != null && !newTable[pos].equals(key)) {
         pos += 2;
         if (pos >= newTable.length) {
            pos = 0;
         }
      }

      newTable[pos] = key;
      newTable[pos + 1] = value;
   }

   public Map<String, V> toMap() {
      Map<String, V> map = new HashMap();
      Object[] t = this.table;

      for(int i = 0; i < t.length; i += 2) {
         if (t[i] != null) {
            map.put((String)t[i], ((SubstringMatch)t[i + 1]).value);
         }
      }

      return map;
   }

   public synchronized void clear() {
      this.size = 0;
      this.table = new Object[16];
   }

   private static int hash(String value, int length) {
      if (length == 0) {
         return 0;
      } else {
         int h = 0;

         for(int i = 0; i < length; ++i) {
            h = 31 * h + value.charAt(i);
         }

         return h;
      }
   }

   public Iterable<String> keys() {
      return new Iterable<String>() {
         public Iterator<String> iterator() {
            final Object[] tMap = SubstringMap.this.table;

            final int i;
            for(i = 0; i < SubstringMap.this.table.length && tMap[i] == null; i += 2) {
            }

            return new Iterator<String>() {
               private Object[] map = tMap;
               private int pos = i;

               public boolean hasNext() {
                  return this.pos < SubstringMap.this.table.length;
               }

               public String next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     String ret = (String)this.map[this.pos];

                     for(this.pos += 2; this.pos < SubstringMap.this.table.length && tMap[this.pos] == null; this.pos += 2) {
                     }

                     return ret;
                  }
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   public static final class SubstringMatch<V> {
      private final String key;
      private final V value;

      public SubstringMatch(String key, V value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }
   }
}
