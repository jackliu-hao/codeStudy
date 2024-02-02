package cn.hutool.core.text.csv;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public final class CsvRow implements List<String> {
   private final long originalLineNumber;
   final Map<String, Integer> headerMap;
   final List<String> fields;

   public CsvRow(long originalLineNumber, Map<String, Integer> headerMap, List<String> fields) {
      Assert.notNull(fields, "fields must be not null!");
      this.originalLineNumber = originalLineNumber;
      this.headerMap = headerMap;
      this.fields = fields;
   }

   public long getOriginalLineNumber() {
      return this.originalLineNumber;
   }

   public String getByName(String name) {
      Assert.notNull(this.headerMap, "No header available!");
      Integer col = (Integer)this.headerMap.get(name);
      return col != null ? this.get(col) : null;
   }

   public List<String> getRawList() {
      return this.fields;
   }

   public Map<String, String> getFieldMap() {
      if (this.headerMap == null) {
         throw new IllegalStateException("No header available");
      } else {
         Map<String, String> fieldMap = new LinkedHashMap(this.headerMap.size(), 1.0F);
         Iterator var5 = this.headerMap.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, Integer> header = (Map.Entry)var5.next();
            String key = (String)header.getKey();
            Integer col = (Integer)this.headerMap.get(key);
            String val = null == col ? null : this.get(col);
            fieldMap.put(key, val);
         }

         return fieldMap;
      }
   }

   public <T> T toBean(Class<T> clazz) {
      return BeanUtil.toBeanIgnoreError(this.getFieldMap(), clazz);
   }

   public int getFieldCount() {
      return this.fields.size();
   }

   public int size() {
      return this.fields.size();
   }

   public boolean isEmpty() {
      return this.fields.isEmpty();
   }

   public boolean contains(Object o) {
      return this.fields.contains(o);
   }

   public Iterator<String> iterator() {
      return this.fields.iterator();
   }

   public Object[] toArray() {
      return this.fields.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return this.fields.toArray(a);
   }

   public boolean add(String e) {
      return this.fields.add(e);
   }

   public boolean remove(Object o) {
      return this.fields.remove(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.fields.containsAll(c);
   }

   public boolean addAll(Collection<? extends String> c) {
      return this.fields.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends String> c) {
      return this.fields.addAll(index, c);
   }

   public boolean removeAll(Collection<?> c) {
      return this.fields.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return this.fields.retainAll(c);
   }

   public void clear() {
      this.fields.clear();
   }

   public String get(int index) {
      return index >= this.fields.size() ? null : (String)this.fields.get(index);
   }

   public String set(int index, String element) {
      return (String)this.fields.set(index, element);
   }

   public void add(int index, String element) {
      this.fields.add(index, element);
   }

   public String remove(int index) {
      return (String)this.fields.remove(index);
   }

   public int indexOf(Object o) {
      return this.fields.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.fields.lastIndexOf(o);
   }

   public ListIterator<String> listIterator() {
      return this.fields.listIterator();
   }

   public ListIterator<String> listIterator(int index) {
      return this.fields.listIterator(index);
   }

   public List<String> subList(int fromIndex, int toIndex) {
      return this.fields.subList(fromIndex, toIndex);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("CsvRow{");
      sb.append("originalLineNumber=");
      sb.append(this.originalLineNumber);
      sb.append(", ");
      sb.append("fields=");
      if (this.headerMap != null) {
         sb.append('{');
         Iterator<Map.Entry<String, String>> it = this.getFieldMap().entrySet().iterator();

         while(it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)it.next();
            sb.append((String)entry.getKey());
            sb.append('=');
            if (entry.getValue() != null) {
               sb.append((String)entry.getValue());
            }

            if (it.hasNext()) {
               sb.append(", ");
            }
         }

         sb.append('}');
      } else {
         sb.append(this.fields.toString());
      }

      sb.append('}');
      return sb.toString();
   }
}
