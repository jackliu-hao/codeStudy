/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubstringMap<V>
/*     */ {
/*     */   private static final int ALL_BUT_LAST_BIT = -2;
/*  41 */   private volatile Object[] table = new Object[16];
/*     */   private int size;
/*     */   
/*     */   public SubstringMatch<V> get(String key, int length) {
/*  45 */     return get(key, length, false);
/*     */   }
/*     */   
/*     */   public SubstringMatch<V> get(String key) {
/*  49 */     return get(key, key.length(), false);
/*     */   }
/*     */   
/*     */   private SubstringMatch<V> get(String key, int length, boolean exact) {
/*  53 */     if (key.length() < length) {
/*  54 */       throw new IllegalArgumentException();
/*     */     }
/*  56 */     Object[] table = this.table;
/*  57 */     int hash = hash(key, length);
/*  58 */     int pos = tablePos(table, hash);
/*  59 */     int start = pos;
/*  60 */     while (table[pos] != null) {
/*  61 */       if (exact) {
/*  62 */         if (table[pos].equals(key)) {
/*  63 */           return (SubstringMatch<V>)table[pos + 1];
/*     */         }
/*     */       }
/*  66 */       else if (doEquals((String)table[pos], key, length)) {
/*  67 */         return (SubstringMatch<V>)table[pos + 1];
/*     */       } 
/*     */       
/*  70 */       pos += 2;
/*  71 */       if (pos >= table.length) {
/*  72 */         pos = 0;
/*     */       }
/*  74 */       if (pos == start) {
/*  75 */         return null;
/*     */       }
/*     */     } 
/*  78 */     return null;
/*     */   }
/*     */   
/*     */   private int tablePos(Object[] table, int hash) {
/*  82 */     return hash & table.length - 1 & 0xFFFFFFFE;
/*     */   }
/*     */   
/*     */   private boolean doEquals(String s1, String s2, int length) {
/*  86 */     if (s1.length() != length || s2.length() < length) {
/*  87 */       return false;
/*     */     }
/*  89 */     for (int i = 0; i < length; i++) {
/*  90 */       if (s1.charAt(i) != s2.charAt(i)) {
/*  91 */         return false;
/*     */       }
/*     */     } 
/*  94 */     return true;
/*     */   }
/*     */   public synchronized void put(String key, V value) {
/*     */     Object[] newTable;
/*  98 */     if (key == null) {
/*  99 */       throw new NullPointerException();
/*     */     }
/*     */     
/* 102 */     if (this.table.length / this.size < 4.0D && this.table.length != Integer.MAX_VALUE) {
/* 103 */       newTable = new Object[this.table.length << 1];
/* 104 */       for (int i = 0; i < this.table.length; i += 2) {
/* 105 */         if (this.table[i] != null) {
/* 106 */           doPut(newTable, (String)this.table[i], this.table[i + 1]);
/*     */         }
/*     */       } 
/*     */     } else {
/* 110 */       newTable = new Object[this.table.length];
/* 111 */       System.arraycopy(this.table, 0, newTable, 0, this.table.length);
/*     */     } 
/* 113 */     doPut(newTable, key, new SubstringMatch<>(key, value));
/* 114 */     this.table = newTable;
/* 115 */     this.size++;
/*     */   }
/*     */   
/*     */   public synchronized V remove(String key) {
/* 119 */     if (key == null) {
/* 120 */       throw new NullPointerException();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 125 */     V value = null;
/* 126 */     Object[] newTable = new Object[this.table.length];
/* 127 */     for (int i = 0; i < this.table.length; i += 2) {
/* 128 */       if (this.table[i] != null && !this.table[i].equals(key)) {
/* 129 */         doPut(newTable, (String)this.table[i], this.table[i + 1]);
/* 130 */       } else if (this.table[i] != null) {
/* 131 */         value = (V)this.table[i + 1];
/* 132 */         this.size--;
/*     */       } 
/*     */     } 
/* 135 */     this.table = newTable;
/* 136 */     if (value == null) {
/* 137 */       return null;
/*     */     }
/* 139 */     return ((SubstringMatch<V>)value).getValue();
/*     */   }
/*     */   
/*     */   private void doPut(Object[] newTable, String key, Object value) {
/* 143 */     int hash = hash(key, key.length());
/* 144 */     int pos = tablePos(newTable, hash);
/* 145 */     while (newTable[pos] != null && !newTable[pos].equals(key)) {
/* 146 */       pos += 2;
/* 147 */       if (pos >= newTable.length) {
/* 148 */         pos = 0;
/*     */       }
/*     */     } 
/* 151 */     newTable[pos] = key;
/* 152 */     newTable[pos + 1] = value;
/*     */   }
/*     */   
/*     */   public Map<String, V> toMap() {
/* 156 */     Map<String, V> map = new HashMap<>();
/* 157 */     Object[] t = this.table;
/* 158 */     for (int i = 0; i < t.length; i += 2) {
/* 159 */       if (t[i] != null) {
/* 160 */         map.put((String)t[i], ((SubstringMatch)t[i + 1]).value);
/*     */       }
/*     */     } 
/* 163 */     return map;
/*     */   }
/*     */   
/*     */   public synchronized void clear() {
/* 167 */     this.size = 0;
/* 168 */     this.table = new Object[16];
/*     */   }
/*     */   
/*     */   private static int hash(String value, int length) {
/* 172 */     if (length == 0) {
/* 173 */       return 0;
/*     */     }
/* 175 */     int h = 0;
/* 176 */     for (int i = 0; i < length; i++) {
/* 177 */       h = 31 * h + value.charAt(i);
/*     */     }
/* 179 */     return h;
/*     */   }
/*     */   
/*     */   public Iterable<String> keys() {
/* 183 */     return new Iterable<String>()
/*     */       {
/*     */         public Iterator<String> iterator() {
/* 186 */           final Object[] tMap = SubstringMap.this.table;
/* 187 */           int i = 0;
/* 188 */           while (i < SubstringMap.this.table.length && tMap[i] == null) {
/* 189 */             i += 2;
/*     */           }
/* 191 */           final int startPos = i;
/*     */           
/* 193 */           return new Iterator<String>()
/*     */             {
/* 195 */               private Object[] map = tMap;
/*     */               
/* 197 */               private int pos = startPos;
/*     */ 
/*     */               
/*     */               public boolean hasNext() {
/* 201 */                 return (this.pos < SubstringMap.this.table.length);
/*     */               }
/*     */ 
/*     */               
/*     */               public String next() {
/* 206 */                 if (!hasNext()) {
/* 207 */                   throw new NoSuchElementException();
/*     */                 }
/* 209 */                 String ret = (String)this.map[this.pos];
/*     */                 
/* 211 */                 this.pos += 2;
/* 212 */                 while (this.pos < SubstringMap.this.table.length && tMap[this.pos] == null) {
/* 213 */                   this.pos += 2;
/*     */                 }
/* 215 */                 return ret;
/*     */               }
/*     */ 
/*     */               
/*     */               public void remove() {
/* 220 */                 throw new UnsupportedOperationException();
/*     */               }
/*     */             };
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public static final class SubstringMatch<V>
/*     */   {
/*     */     private final String key;
/*     */     private final V value;
/*     */     
/*     */     public SubstringMatch(String key, V value) {
/* 233 */       this.key = key;
/* 234 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getKey() {
/* 238 */       return this.key;
/*     */     }
/*     */     
/*     */     public V getValue() {
/* 242 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\SubstringMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */