package cn.hutool.core.map.multi;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.ComputeIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.TransIter;
import cn.hutool.core.map.AbsEntry;
import cn.hutool.core.map.MapUtil;
import java.lang.invoke.SerializedLambda;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RowKeyTable<R, C, V> extends AbsTable<R, C, V> {
   final Map<R, Map<C, V>> raw;
   final Builder<? extends Map<C, V>> columnBuilder;
   private Map<C, Map<R, V>> columnMap;
   private Set<C> columnKeySet;

   public RowKeyTable() {
      this(new HashMap());
   }

   public RowKeyTable(boolean isLinked) {
      this(MapUtil.newHashMap(isLinked), () -> {
         return MapUtil.newHashMap(isLinked);
      });
   }

   public RowKeyTable(Map<R, Map<C, V>> raw) {
      this(raw, HashMap::new);
   }

   public RowKeyTable(Map<R, Map<C, V>> raw, Builder<? extends Map<C, V>> columnMapBuilder) {
      this.raw = raw;
      this.columnBuilder = null == columnMapBuilder ? HashMap::new : columnMapBuilder;
   }

   public Map<R, Map<C, V>> rowMap() {
      return this.raw;
   }

   public V put(R rowKey, C columnKey, V value) {
      return ((Map)this.raw.computeIfAbsent(rowKey, (key) -> {
         return (Map)this.columnBuilder.build();
      })).put(columnKey, value);
   }

   public V remove(R rowKey, C columnKey) {
      Map<C, V> map = this.getRow(rowKey);
      if (null == map) {
         return null;
      } else {
         V value = map.remove(columnKey);
         if (map.isEmpty()) {
            this.raw.remove(rowKey);
         }

         return value;
      }
   }

   public boolean isEmpty() {
      return this.raw.isEmpty();
   }

   public void clear() {
      this.raw.clear();
   }

   public boolean containsColumn(C columnKey) {
      if (columnKey == null) {
         return false;
      } else {
         Iterator var2 = this.raw.values().iterator();

         Map map;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            map = (Map)var2.next();
         } while(null == map || !map.containsKey(columnKey));

         return true;
      }
   }

   public Map<C, Map<R, V>> columnMap() {
      Map<C, Map<R, V>> result = this.columnMap;
      return result == null ? (this.columnMap = new ColumnMap()) : result;
   }

   public Set<C> columnKeySet() {
      Set<C> result = this.columnKeySet;
      return result == null ? (this.columnKeySet = new ColumnKeySet()) : result;
   }

   public List<C> columnKeys() {
      Collection<Map<C, V>> values = this.raw.values();
      List<C> result = new ArrayList(values.size() * 16);
      Iterator var3 = values.iterator();

      while(var3.hasNext()) {
         Map<C, V> map = (Map)var3.next();
         map.forEach((key, value) -> {
            result.add(key);
         });
      }

      return result;
   }

   public Map<R, V> getColumn(C columnKey) {
      return new Column(columnKey);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$new$9e12e91d$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals("build") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/map/multi/RowKeyTable") && lambda.getImplMethodSignature().equals("(Z)Ljava/util/Map;")) {
               return () -> {
                  return MapUtil.newHashMap(isLinked);
               };
            }
            break;
         case "<init>":
            if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals("build") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/HashMap") && lambda.getImplMethodSignature().equals("()V")) {
               return HashMap::new;
            }

            if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals("build") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/HashMap") && lambda.getImplMethodSignature().equals("()V")) {
               return HashMap::new;
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }

   private class Column extends AbstractMap<R, V> {
      final C columnKey;

      Column(C columnKey) {
         this.columnKey = columnKey;
      }

      public Set<Map.Entry<R, V>> entrySet() {
         return new EntrySet();
      }

      private class EntrySetIterator extends ComputeIter<Map.Entry<R, V>> {
         final Iterator<Map.Entry<R, Map<C, V>>> iterator;

         private EntrySetIterator() {
            this.iterator = RowKeyTable.this.raw.entrySet().iterator();
         }

         protected Map.Entry<R, V> computeNext() {
            while(true) {
               if (this.iterator.hasNext()) {
                  final Map.Entry<R, Map<C, V>> entry = (Map.Entry)this.iterator.next();
                  if (!((Map)entry.getValue()).containsKey(Column.this.columnKey)) {
                     continue;
                  }

                  return new AbsEntry<R, V>() {
                     public R getKey() {
                        return entry.getKey();
                     }

                     public V getValue() {
                        return ((Map)entry.getValue()).get(Column.this.columnKey);
                     }

                     public V setValue(V value) {
                        return ((Map)entry.getValue()).put(Column.this.columnKey, value);
                     }
                  };
               }

               return null;
            }
         }

         // $FF: synthetic method
         EntrySetIterator(Object x1) {
            this();
         }
      }

      private class EntrySet extends AbstractSet<Map.Entry<R, V>> {
         private EntrySet() {
         }

         public Iterator<Map.Entry<R, V>> iterator() {
            return Column.this.new EntrySetIterator();
         }

         public int size() {
            int size = 0;
            Iterator var2 = RowKeyTable.this.raw.values().iterator();

            while(var2.hasNext()) {
               Map<C, V> map = (Map)var2.next();
               if (map.containsKey(Column.this.columnKey)) {
                  ++size;
               }
            }

            return size;
         }

         // $FF: synthetic method
         EntrySet(Object x1) {
            this();
         }
      }
   }

   private class ColumnKeyIterator extends ComputeIter<C> {
      final Map<C, V> seen;
      final Iterator<Map<C, V>> mapIterator;
      Iterator<Map.Entry<C, V>> entryIterator;

      private ColumnKeyIterator() {
         this.seen = (Map)RowKeyTable.this.columnBuilder.build();
         this.mapIterator = RowKeyTable.this.raw.values().iterator();
         this.entryIterator = IterUtil.empty();
      }

      protected C computeNext() {
         while(true) {
            if (this.entryIterator.hasNext()) {
               Map.Entry<C, V> entry = (Map.Entry)this.entryIterator.next();
               if (!this.seen.containsKey(entry.getKey())) {
                  this.seen.put(entry.getKey(), entry.getValue());
                  return entry.getKey();
               }
            } else {
               if (!this.mapIterator.hasNext()) {
                  return null;
               }

               this.entryIterator = ((Map)this.mapIterator.next()).entrySet().iterator();
            }
         }
      }

      // $FF: synthetic method
      ColumnKeyIterator(Object x1) {
         this();
      }
   }

   private class ColumnKeySet extends AbstractSet<C> {
      private ColumnKeySet() {
      }

      public Iterator<C> iterator() {
         return RowKeyTable.this.new ColumnKeyIterator();
      }

      public int size() {
         return IterUtil.size(this.iterator());
      }

      // $FF: synthetic method
      ColumnKeySet(Object x1) {
         this();
      }
   }

   private class ColumnMapEntrySet extends AbstractSet<Map.Entry<C, Map<R, V>>> {
      private final Set<C> columnKeySet;

      private ColumnMapEntrySet() {
         this.columnKeySet = RowKeyTable.this.columnKeySet();
      }

      public Iterator<Map.Entry<C, Map<R, V>>> iterator() {
         return new TransIter(this.columnKeySet.iterator(), (c) -> {
            return MapUtil.entry(c, RowKeyTable.this.getColumn(c));
         });
      }

      public int size() {
         return this.columnKeySet.size();
      }

      // $FF: synthetic method
      ColumnMapEntrySet(Object x1) {
         this();
      }
   }

   private class ColumnMap extends AbstractMap<C, Map<R, V>> {
      private ColumnMap() {
      }

      public Set<Map.Entry<C, Map<R, V>>> entrySet() {
         return RowKeyTable.this.new ColumnMapEntrySet();
      }

      // $FF: synthetic method
      ColumnMap(Object x1) {
         this();
      }
   }
}
