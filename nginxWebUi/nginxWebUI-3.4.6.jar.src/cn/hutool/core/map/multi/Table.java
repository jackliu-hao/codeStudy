/*     */ package cn.hutool.core.map.multi;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.lang.Opt;
/*     */ import cn.hutool.core.lang.func.Consumer3;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ public interface Table<R, C, V>
/*     */   extends Iterable<Table.Cell<R, C, V>>
/*     */ {
/*     */   default boolean contains(R rowKey, C columnKey) {
/*  34 */     return ((Boolean)Opt.ofNullable(getRow(rowKey)).map(map -> Boolean.valueOf(map.containsKey(columnKey))).get()).booleanValue();
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
/*     */   default boolean containsRow(R rowKey) {
/*  46 */     return ((Boolean)Opt.ofNullable(rowMap()).map(map -> Boolean.valueOf(map.containsKey(rowKey))).get()).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Map<C, V> getRow(R rowKey) {
/*  56 */     return (Map<C, V>)Opt.ofNullable(rowMap()).map(map -> (Map)map.get(rowKey)).get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Set<R> rowKeySet() {
/*  65 */     return (Set<R>)Opt.ofNullable(rowMap()).map(Map::keySet).get();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean containsColumn(C columnKey) {
/*  85 */     return ((Boolean)Opt.ofNullable(columnMap()).map(map -> Boolean.valueOf(map.containsKey(columnKey))).get()).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Map<R, V> getColumn(C columnKey) {
/*  95 */     return (Map<R, V>)Opt.ofNullable(columnMap()).map(map -> (Map)map.get(columnKey)).get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Set<C> columnKeySet() {
/* 104 */     return (Set<C>)Opt.ofNullable(columnMap()).map(Map::keySet).get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default List<C> columnKeys() {
/* 114 */     Map<C, Map<R, V>> columnMap = columnMap();
/* 115 */     if (MapUtil.isEmpty(columnMap)) {
/* 116 */       return ListUtil.empty();
/*     */     }
/*     */     
/* 119 */     List<C> result = new ArrayList<>(columnMap.size());
/* 120 */     for (Map.Entry<C, Map<R, V>> cMapEntry : columnMap.entrySet()) {
/* 121 */       result.add(cMapEntry.getKey());
/*     */     }
/* 123 */     return result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean containsValue(V value) {
/* 143 */     Collection<Map<C, V>> rows = (Collection<Map<C, V>>)Opt.ofNullable(rowMap()).map(Map::values).get();
/* 144 */     if (null != rows) {
/* 145 */       for (Map<C, V> row : rows) {
/* 146 */         if (row.containsValue(value)) {
/* 147 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 151 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default V get(R rowKey, C columnKey) {
/* 162 */     return (V)Opt.ofNullable(getRow(rowKey)).map(map -> map.get(columnKey)).get();
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
/*     */   default void putAll(Table<? extends R, ? extends C, ? extends V> table) {
/* 196 */     if (null != table) {
/* 197 */       for (Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
/* 198 */         put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */       }
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
/*     */   default int size() {
/* 225 */     Map<R, Map<C, V>> rowMap = rowMap();
/* 226 */     if (MapUtil.isEmpty(rowMap)) {
/* 227 */       return 0;
/*     */     }
/* 229 */     int size = 0;
/* 230 */     for (Map<C, V> map : rowMap.values()) {
/* 231 */       size += map.size();
/*     */     }
/* 233 */     return size;
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
/*     */ 
/*     */   
/*     */   default void forEach(Consumer3<? super R, ? super C, ? super V> consumer) {
/* 247 */     for (Cell<R, C, V> cell : this)
/* 248 */       consumer.accept(cell.getRowKey(), cell.getColumnKey(), cell.getValue()); 
/*     */   }
/*     */   
/*     */   Map<R, Map<C, V>> rowMap();
/*     */   
/*     */   Map<C, Map<R, V>> columnMap();
/*     */   
/*     */   Collection<V> values();
/*     */   
/*     */   Set<Cell<R, C, V>> cellSet();
/*     */   
/*     */   V put(R paramR, C paramC, V paramV);
/*     */   
/*     */   V remove(R paramR, C paramC);
/*     */   
/*     */   boolean isEmpty();
/*     */   
/*     */   void clear();
/*     */   
/*     */   public static interface Cell<R, C, V> {
/*     */     R getRowKey();
/*     */     
/*     */     C getColumnKey();
/*     */     
/*     */     V getValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\Table.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */