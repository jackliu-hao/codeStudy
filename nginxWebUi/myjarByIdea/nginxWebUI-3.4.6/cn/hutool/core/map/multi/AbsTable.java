package cn.hutool.core.map.multi;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.TransIter;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbsTable<R, C, V> implements Table<R, C, V> {
   private Collection<V> values;
   private Set<Table.Cell<R, C, V>> cellSet;

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof Table) {
         Table<?, ?, ?> that = (Table)obj;
         return this.cellSet().equals(that.cellSet());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.cellSet().hashCode();
   }

   public String toString() {
      return this.rowMap().toString();
   }

   public Collection<V> values() {
      Collection<V> result = this.values;
      return result == null ? (this.values = new Values()) : result;
   }

   public Set<Table.Cell<R, C, V>> cellSet() {
      Set<Table.Cell<R, C, V>> result = this.cellSet;
      return result == null ? (this.cellSet = new CellSet()) : result;
   }

   public Iterator<Table.Cell<R, C, V>> iterator() {
      return new CellIterator();
   }

   private static class SimpleCell<R, C, V> implements Table.Cell<R, C, V>, Serializable {
      private static final long serialVersionUID = 1L;
      private final R rowKey;
      private final C columnKey;
      private final V value;

      SimpleCell(R rowKey, C columnKey, V value) {
         this.rowKey = rowKey;
         this.columnKey = columnKey;
         this.value = value;
      }

      public R getRowKey() {
         return this.rowKey;
      }

      public C getColumnKey() {
         return this.columnKey;
      }

      public V getValue() {
         return this.value;
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell<?, ?, ?> other = (Table.Cell)obj;
            return ObjectUtil.equal(this.rowKey, other.getRowKey()) && ObjectUtil.equal(this.columnKey, other.getColumnKey()) && ObjectUtil.equal(this.value, other.getValue());
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.rowKey, this.columnKey, this.value});
      }

      public String toString() {
         return "(" + this.rowKey + "," + this.columnKey + ")=" + this.value;
      }
   }

   private class CellIterator implements Iterator<Table.Cell<R, C, V>> {
      final Iterator<Map.Entry<R, Map<C, V>>> rowIterator;
      Map.Entry<R, Map<C, V>> rowEntry;
      Iterator<Map.Entry<C, V>> columnIterator;

      private CellIterator() {
         this.rowIterator = AbsTable.this.rowMap().entrySet().iterator();
         this.columnIterator = IterUtil.empty();
      }

      public boolean hasNext() {
         return this.rowIterator.hasNext() || this.columnIterator.hasNext();
      }

      public Table.Cell<R, C, V> next() {
         if (!this.columnIterator.hasNext()) {
            this.rowEntry = (Map.Entry)this.rowIterator.next();
            this.columnIterator = ((Map)this.rowEntry.getValue()).entrySet().iterator();
         }

         Map.Entry<C, V> columnEntry = (Map.Entry)this.columnIterator.next();
         return new SimpleCell(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
      }

      public void remove() {
         this.columnIterator.remove();
         if (((Map)this.rowEntry.getValue()).isEmpty()) {
            this.rowIterator.remove();
         }

      }

      // $FF: synthetic method
      CellIterator(Object x1) {
         this();
      }
   }

   private class CellSet extends AbstractSet<Table.Cell<R, C, V>> {
      private CellSet() {
      }

      public boolean contains(Object o) {
         if (o instanceof Table.Cell) {
            Table.Cell<R, C, V> cell = (Table.Cell)o;
            Map<C, V> row = AbsTable.this.getRow(cell.getRowKey());
            if (null != row) {
               return ObjectUtil.equals(row.get(cell.getColumnKey()), cell.getValue());
            }
         }

         return false;
      }

      public boolean remove(Object o) {
         if (this.contains(o)) {
            Table.Cell<R, C, V> cell = (Table.Cell)o;
            AbsTable.this.remove(cell.getRowKey(), cell.getColumnKey());
         }

         return false;
      }

      public void clear() {
         AbsTable.this.clear();
      }

      public Iterator<Table.Cell<R, C, V>> iterator() {
         return AbsTable.this.new CellIterator();
      }

      public int size() {
         return AbsTable.this.size();
      }

      // $FF: synthetic method
      CellSet(Object x1) {
         this();
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      public Iterator<V> iterator() {
         return new TransIter(AbsTable.this.cellSet().iterator(), Table.Cell::getValue);
      }

      public boolean contains(Object o) {
         return AbsTable.this.containsValue(o);
      }

      public void clear() {
         AbsTable.this.clear();
      }

      public int size() {
         return AbsTable.this.size();
      }

      // $FF: synthetic method
      Values(Object x1) {
         this();
      }
   }
}
