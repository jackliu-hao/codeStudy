/*     */ package cn.hutool.core.map.multi;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.collection.ComputeIter;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.collection.TransIter;
/*     */ import cn.hutool.core.map.AbsEntry;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class RowKeyTable<R, C, V>
/*     */   extends AbsTable<R, C, V>
/*     */ {
/*     */   final Map<R, Map<C, V>> raw;
/*     */   final Builder<? extends Map<C, V>> columnBuilder;
/*     */   private Map<C, Map<R, V>> columnMap;
/*     */   private Set<C> columnKeySet;
/*     */   
/*     */   public RowKeyTable() {
/*  44 */     this(new HashMap<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowKeyTable(boolean isLinked) {
/*  54 */     this(MapUtil.newHashMap(isLinked), () -> MapUtil.newHashMap(isLinked));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowKeyTable(Map<R, Map<C, V>> raw) {
/*  63 */     this(raw, HashMap::new);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowKeyTable(Map<R, Map<C, V>> raw, Builder<? extends Map<C, V>> columnMapBuilder) {
/*  73 */     this.raw = raw;
/*  74 */     this.columnBuilder = (null == columnMapBuilder) ? HashMap::new : columnMapBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<R, Map<C, V>> rowMap() {
/*  80 */     return this.raw;
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(R rowKey, C columnKey, V value) {
/*  85 */     return ((Map<C, V>)this.raw.computeIfAbsent(rowKey, key -> (Map)this.columnBuilder.build())).put(columnKey, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(R rowKey, C columnKey) {
/*  90 */     Map<C, V> map = getRow(rowKey);
/*  91 */     if (null == map) {
/*  92 */       return null;
/*     */     }
/*  94 */     V value = map.remove(columnKey);
/*  95 */     if (map.isEmpty()) {
/*  96 */       this.raw.remove(rowKey);
/*     */     }
/*  98 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 103 */     return this.raw.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 108 */     this.raw.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsColumn(C columnKey) {
/* 113 */     if (columnKey == null) {
/* 114 */       return false;
/*     */     }
/* 116 */     for (Map<C, V> map : this.raw.values()) {
/* 117 */       if (null != map && map.containsKey(columnKey)) {
/* 118 */         return true;
/*     */       }
/*     */     } 
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<C, Map<R, V>> columnMap() {
/* 127 */     Map<C, Map<R, V>> result = this.columnMap;
/* 128 */     return (result == null) ? (this.columnMap = new ColumnMap()) : result;
/*     */   }
/*     */   
/*     */   private class ColumnMap
/*     */     extends AbstractMap<C, Map<R, V>> {
/*     */     private ColumnMap() {}
/*     */     
/*     */     public Set<Map.Entry<C, Map<R, V>>> entrySet() {
/* 136 */       return new RowKeyTable.ColumnMapEntrySet();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ColumnMapEntrySet extends AbstractSet<Map.Entry<C, Map<R, V>>> {
/* 141 */     private final Set<C> columnKeySet = RowKeyTable.this.columnKeySet();
/*     */ 
/*     */     
/*     */     public Iterator<Map.Entry<C, Map<R, V>>> iterator() {
/* 145 */       return (Iterator<Map.Entry<C, Map<R, V>>>)new TransIter(this.columnKeySet.iterator(), c -> MapUtil.entry(c, RowKeyTable.this.getColumn(c)));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() {
/* 151 */       return this.columnKeySet.size();
/*     */     }
/*     */ 
/*     */     
/*     */     private ColumnMapEntrySet() {}
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<C> columnKeySet() {
/* 160 */     Set<C> result = this.columnKeySet;
/* 161 */     return (result == null) ? (this.columnKeySet = new ColumnKeySet()) : result;
/*     */   }
/*     */   
/*     */   private class ColumnKeySet
/*     */     extends AbstractSet<C>
/*     */   {
/*     */     private ColumnKeySet() {}
/*     */     
/*     */     public Iterator<C> iterator() {
/* 170 */       return (Iterator<C>)new RowKeyTable.ColumnKeyIterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 175 */       return IterUtil.size(iterator());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ColumnKeyIterator extends ComputeIter<C> {
/* 180 */     final Map<C, V> seen = (Map<C, V>)RowKeyTable.this.columnBuilder.build();
/* 181 */     final Iterator<Map<C, V>> mapIterator = RowKeyTable.this.raw.values().iterator();
/* 182 */     Iterator<Map.Entry<C, V>> entryIterator = IterUtil.empty();
/*     */ 
/*     */     
/*     */     protected C computeNext() {
/*     */       while (true) {
/* 187 */         while (this.entryIterator.hasNext()) {
/* 188 */           Map.Entry<C, V> entry = this.entryIterator.next();
/* 189 */           if (false == this.seen.containsKey(entry.getKey())) {
/* 190 */             this.seen.put(entry.getKey(), entry.getValue());
/* 191 */             return entry.getKey();
/*     */           } 
/* 193 */         }  if (this.mapIterator.hasNext()) {
/* 194 */           this.entryIterator = ((Map<C, V>)this.mapIterator.next()).entrySet().iterator(); continue;
/*     */         }  break;
/* 196 */       }  return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private ColumnKeyIterator() {}
/*     */   }
/*     */ 
/*     */   
/*     */   public List<C> columnKeys() {
/* 206 */     Collection<Map<C, V>> values = this.raw.values();
/* 207 */     List<C> result = new ArrayList<>(values.size() * 16);
/* 208 */     for (Map<C, V> map : values) {
/* 209 */       map.forEach((key, value) -> result.add(key));
/*     */     }
/* 211 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<R, V> getColumn(C columnKey) {
/* 216 */     return new Column(columnKey);
/*     */   }
/*     */   
/*     */   private class Column extends AbstractMap<R, V> {
/*     */     final C columnKey;
/*     */     
/*     */     Column(C columnKey) {
/* 223 */       this.columnKey = columnKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<Map.Entry<R, V>> entrySet() {
/* 228 */       return new EntrySet();
/*     */     }
/*     */     
/*     */     private class EntrySet extends AbstractSet<Map.Entry<R, V>> {
/*     */       private EntrySet() {}
/*     */       
/*     */       public Iterator<Map.Entry<R, V>> iterator() {
/* 235 */         return (Iterator<Map.Entry<R, V>>)new RowKeyTable.Column.EntrySetIterator();
/*     */       }
/*     */ 
/*     */       
/*     */       public int size() {
/* 240 */         int size = 0;
/* 241 */         for (Map<C, V> map : (Iterable<Map<C, V>>)RowKeyTable.this.raw.values()) {
/* 242 */           if (map.containsKey(RowKeyTable.Column.this.columnKey)) {
/* 243 */             size++;
/*     */           }
/*     */         } 
/* 246 */         return size;
/*     */       }
/*     */     }
/*     */     
/*     */     private class EntrySetIterator extends ComputeIter<Map.Entry<R, V>> {
/* 251 */       final Iterator<Map.Entry<R, Map<C, V>>> iterator = RowKeyTable.this.raw.entrySet().iterator();
/*     */ 
/*     */       
/*     */       protected Map.Entry<R, V> computeNext() {
/* 255 */         while (this.iterator.hasNext()) {
/* 256 */           final Map.Entry<R, Map<C, V>> entry = this.iterator.next();
/* 257 */           if (((Map)entry.getValue()).containsKey(RowKeyTable.Column.this.columnKey)) {
/* 258 */             return (Map.Entry<R, V>)new AbsEntry<R, V>()
/*     */               {
/*     */                 public R getKey() {
/* 261 */                   return (R)entry.getKey();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public V getValue() {
/* 266 */                   return (V)((Map)entry.getValue()).get(RowKeyTable.Column.this.columnKey);
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public V setValue(V value) {
/* 271 */                   return ((Map<C, V>)entry.getValue()).put(RowKeyTable.Column.this.columnKey, value);
/*     */                 }
/*     */               };
/*     */           }
/*     */         } 
/* 276 */         return null;
/*     */       }
/*     */       
/*     */       private EntrySetIterator() {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\RowKeyTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */