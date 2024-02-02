package cn.hutool.core.map.multi;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.func.Consumer3;
import cn.hutool.core.map.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Table<R, C, V> extends Iterable<Cell<R, C, V>> {
   default boolean contains(R rowKey, C columnKey) {
      return (Boolean)Opt.ofNullable(this.getRow(rowKey)).map((map) -> {
         return map.containsKey(columnKey);
      }).get();
   }

   default boolean containsRow(R rowKey) {
      return (Boolean)Opt.ofNullable(this.rowMap()).map((map) -> {
         return map.containsKey(rowKey);
      }).get();
   }

   default Map<C, V> getRow(R rowKey) {
      return (Map)Opt.ofNullable(this.rowMap()).map((map) -> {
         return (Map)map.get(rowKey);
      }).get();
   }

   default Set<R> rowKeySet() {
      return (Set)Opt.ofNullable(this.rowMap()).map(Map::keySet).get();
   }

   Map<R, Map<C, V>> rowMap();

   default boolean containsColumn(C columnKey) {
      return (Boolean)Opt.ofNullable(this.columnMap()).map((map) -> {
         return map.containsKey(columnKey);
      }).get();
   }

   default Map<R, V> getColumn(C columnKey) {
      return (Map)Opt.ofNullable(this.columnMap()).map((map) -> {
         return (Map)map.get(columnKey);
      }).get();
   }

   default Set<C> columnKeySet() {
      return (Set)Opt.ofNullable(this.columnMap()).map(Map::keySet).get();
   }

   default List<C> columnKeys() {
      Map<C, Map<R, V>> columnMap = this.columnMap();
      if (MapUtil.isEmpty(columnMap)) {
         return ListUtil.empty();
      } else {
         List<C> result = new ArrayList(columnMap.size());
         Iterator var3 = columnMap.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<C, Map<R, V>> cMapEntry = (Map.Entry)var3.next();
            result.add(cMapEntry.getKey());
         }

         return result;
      }
   }

   Map<C, Map<R, V>> columnMap();

   default boolean containsValue(V value) {
      Collection<Map<C, V>> rows = (Collection)Opt.ofNullable(this.rowMap()).map(Map::values).get();
      if (null != rows) {
         Iterator var3 = rows.iterator();

         while(var3.hasNext()) {
            Map<C, V> row = (Map)var3.next();
            if (row.containsValue(value)) {
               return true;
            }
         }
      }

      return false;
   }

   default V get(R rowKey, C columnKey) {
      return Opt.ofNullable(this.getRow(rowKey)).map((map) -> {
         return map.get(columnKey);
      }).get();
   }

   Collection<V> values();

   Set<Cell<R, C, V>> cellSet();

   V put(R var1, C var2, V var3);

   default void putAll(Table<? extends R, ? extends C, ? extends V> table) {
      if (null != table) {
         Iterator var2 = table.cellSet().iterator();

         while(var2.hasNext()) {
            Cell<? extends R, ? extends C, ? extends V> cell = (Cell)var2.next();
            this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
         }
      }

   }

   V remove(R var1, C var2);

   boolean isEmpty();

   default int size() {
      Map<R, Map<C, V>> rowMap = this.rowMap();
      if (MapUtil.isEmpty(rowMap)) {
         return 0;
      } else {
         int size = 0;

         Map map;
         for(Iterator var3 = rowMap.values().iterator(); var3.hasNext(); size += map.size()) {
            map = (Map)var3.next();
         }

         return size;
      }
   }

   void clear();

   default void forEach(Consumer3<? super R, ? super C, ? super V> consumer) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Cell<R, C, V> cell = (Cell)var2.next();
         consumer.accept(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
      }

   }

   public interface Cell<R, C, V> {
      R getRowKey();

      C getColumnKey();

      V getValue();
   }
}
