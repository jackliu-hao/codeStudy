/*     */ package cn.hutool.core.map.multi;
/*     */ 
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.collection.TransIter;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public abstract class AbsTable<R, C, V>
/*     */   implements Table<R, C, V>
/*     */ {
/*     */   private Collection<V> values;
/*     */   private Set<Table.Cell<R, C, V>> cellSet;
/*     */   
/*     */   public boolean equals(Object obj) {
/*  38 */     if (obj == this)
/*  39 */       return true; 
/*  40 */     if (obj instanceof Table) {
/*  41 */       Table<?, ?, ?> that = (Table<?, ?, ?>)obj;
/*  42 */       return cellSet().equals(that.cellSet());
/*     */     } 
/*  44 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  50 */     return cellSet().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  55 */     return rowMap().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/*  61 */     Collection<V> result = this.values;
/*  62 */     return (result == null) ? (this.values = new Values()) : result;
/*     */   }
/*     */   
/*     */   private class Values extends AbstractCollection<V> {
/*     */     private Values() {}
/*     */     
/*     */     public Iterator<V> iterator() {
/*  69 */       return (Iterator<V>)new TransIter(AbsTable.this.cellSet().iterator(), Table.Cell::getValue);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean contains(Object o) {
/*  75 */       return AbsTable.this.containsValue(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() {
/*  80 */       AbsTable.this.clear();
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/*  85 */       return AbsTable.this.size();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Table.Cell<R, C, V>> cellSet() {
/*  93 */     Set<Table.Cell<R, C, V>> result = this.cellSet;
/*  94 */     return (result == null) ? (this.cellSet = new CellSet()) : result;
/*     */   }
/*     */   
/*     */   private class CellSet
/*     */     extends AbstractSet<Table.Cell<R, C, V>> {
/*     */     private CellSet() {}
/*     */     
/*     */     public boolean contains(Object o) {
/* 102 */       if (o instanceof Table.Cell) {
/* 103 */         Table.Cell<R, C, V> cell = (Table.Cell<R, C, V>)o;
/* 104 */         Map<C, V> row = AbsTable.this.getRow(cell.getRowKey());
/* 105 */         if (null != row) {
/* 106 */           return ObjectUtil.equals(row.get(cell.getColumnKey()), cell.getValue());
/*     */         }
/*     */       } 
/* 109 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean remove(Object o) {
/* 114 */       if (contains(o)) {
/* 115 */         Table.Cell<R, C, V> cell = (Table.Cell<R, C, V>)o;
/* 116 */         AbsTable.this.remove(cell.getRowKey(), cell.getColumnKey());
/*     */       } 
/* 118 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() {
/* 123 */       AbsTable.this.clear();
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<Table.Cell<R, C, V>> iterator() {
/* 128 */       return new AbsTable.CellIterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 133 */       return AbsTable.this.size();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Table.Cell<R, C, V>> iterator() {
/* 141 */     return new CellIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   private class CellIterator
/*     */     implements Iterator<Table.Cell<R, C, V>>
/*     */   {
/* 148 */     final Iterator<Map.Entry<R, Map<C, V>>> rowIterator = AbsTable.this.rowMap().entrySet().iterator();
/*     */     Map.Entry<R, Map<C, V>> rowEntry;
/* 150 */     Iterator<Map.Entry<C, V>> columnIterator = IterUtil.empty();
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 154 */       return (this.rowIterator.hasNext() || this.columnIterator.hasNext());
/*     */     }
/*     */ 
/*     */     
/*     */     public Table.Cell<R, C, V> next() {
/* 159 */       if (false == this.columnIterator.hasNext()) {
/* 160 */         this.rowEntry = this.rowIterator.next();
/* 161 */         this.columnIterator = ((Map<C, V>)this.rowEntry.getValue()).entrySet().iterator();
/*     */       } 
/* 163 */       Map.Entry<C, V> columnEntry = this.columnIterator.next();
/* 164 */       return new AbsTable.SimpleCell<>(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 169 */       this.columnIterator.remove();
/* 170 */       if (((Map)this.rowEntry.getValue()).isEmpty()) {
/* 171 */         this.rowIterator.remove();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private CellIterator() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SimpleCell<R, C, V>
/*     */     implements Table.Cell<R, C, V>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     private final R rowKey;
/*     */     
/*     */     private final C columnKey;
/*     */     
/*     */     private final V value;
/*     */     
/*     */     SimpleCell(R rowKey, C columnKey, V value) {
/* 192 */       this.rowKey = rowKey;
/* 193 */       this.columnKey = columnKey;
/* 194 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public R getRowKey() {
/* 199 */       return this.rowKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public C getColumnKey() {
/* 204 */       return this.columnKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public V getValue() {
/* 209 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 214 */       if (obj == this) {
/* 215 */         return true;
/*     */       }
/* 217 */       if (obj instanceof Table.Cell) {
/* 218 */         Table.Cell<?, ?, ?> other = (Table.Cell<?, ?, ?>)obj;
/* 219 */         return (ObjectUtil.equal(this.rowKey, other.getRowKey()) && 
/* 220 */           ObjectUtil.equal(this.columnKey, other.getColumnKey()) && 
/* 221 */           ObjectUtil.equal(this.value, other.getValue()));
/*     */       } 
/* 223 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 228 */       return Objects.hash(new Object[] { this.rowKey, this.columnKey, this.value });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 233 */       return "(" + this.rowKey + "," + this.columnKey + ")=" + this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\AbsTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */