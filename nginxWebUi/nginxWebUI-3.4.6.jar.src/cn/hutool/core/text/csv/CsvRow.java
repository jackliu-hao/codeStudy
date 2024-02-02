/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
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
/*     */ public final class CsvRow
/*     */   implements List<String>
/*     */ {
/*     */   private final long originalLineNumber;
/*     */   final Map<String, Integer> headerMap;
/*     */   final List<String> fields;
/*     */   
/*     */   public CsvRow(long originalLineNumber, Map<String, Integer> headerMap, List<String> fields) {
/*  34 */     Assert.notNull(fields, "fields must be not null!", new Object[0]);
/*  35 */     this.originalLineNumber = originalLineNumber;
/*  36 */     this.headerMap = headerMap;
/*  37 */     this.fields = fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getOriginalLineNumber() {
/*  46 */     return this.originalLineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getByName(String name) {
/*  57 */     Assert.notNull(this.headerMap, "No header available!", new Object[0]);
/*     */     
/*  59 */     Integer col = this.headerMap.get(name);
/*  60 */     if (col != null) {
/*  61 */       return get(col.intValue());
/*     */     }
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getRawList() {
/*  72 */     return this.fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getFieldMap() {
/*  82 */     if (this.headerMap == null) {
/*  83 */       throw new IllegalStateException("No header available");
/*     */     }
/*     */     
/*  86 */     Map<String, String> fieldMap = new LinkedHashMap<>(this.headerMap.size(), 1.0F);
/*     */ 
/*     */ 
/*     */     
/*  90 */     for (Map.Entry<String, Integer> header : this.headerMap.entrySet()) {
/*  91 */       String key = header.getKey();
/*  92 */       Integer col = this.headerMap.get(key);
/*  93 */       String val = (null == col) ? null : get(col.intValue());
/*  94 */       fieldMap.put(key, val);
/*     */     } 
/*     */     
/*  97 */     return fieldMap;
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
/*     */   public <T> T toBean(Class<T> clazz) {
/* 109 */     return (T)BeanUtil.toBeanIgnoreError(getFieldMap(), clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFieldCount() {
/* 118 */     return this.fields.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 123 */     return this.fields.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 128 */     return this.fields.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 133 */     return this.fields.contains(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> iterator() {
/* 138 */     return this.fields.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 143 */     return this.fields.toArray();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/* 149 */     return this.fields.toArray(a);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(String e) {
/* 154 */     return this.fields.add(e);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 159 */     return this.fields.remove(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/* 164 */     return this.fields.containsAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends String> c) {
/* 169 */     return this.fields.addAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends String> c) {
/* 174 */     return this.fields.addAll(index, c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 179 */     return this.fields.removeAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 184 */     return this.fields.retainAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 189 */     this.fields.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(int index) {
/* 194 */     return (index >= this.fields.size()) ? null : this.fields.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public String set(int index, String element) {
/* 199 */     return this.fields.set(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, String element) {
/* 204 */     this.fields.add(index, element);
/*     */   }
/*     */ 
/*     */   
/*     */   public String remove(int index) {
/* 209 */     return this.fields.remove(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int indexOf(Object o) {
/* 214 */     return this.fields.indexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 219 */     return this.fields.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<String> listIterator() {
/* 224 */     return this.fields.listIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<String> listIterator(int index) {
/* 229 */     return this.fields.listIterator(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> subList(int fromIndex, int toIndex) {
/* 234 */     return this.fields.subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 239 */     StringBuilder sb = new StringBuilder("CsvRow{");
/* 240 */     sb.append("originalLineNumber=");
/* 241 */     sb.append(this.originalLineNumber);
/* 242 */     sb.append(", ");
/*     */     
/* 244 */     sb.append("fields=");
/* 245 */     if (this.headerMap != null) {
/* 246 */       sb.append('{');
/* 247 */       for (Iterator<Map.Entry<String, String>> it = getFieldMap().entrySet().iterator(); it.hasNext(); ) {
/*     */         
/* 249 */         Map.Entry<String, String> entry = it.next();
/* 250 */         sb.append(entry.getKey());
/* 251 */         sb.append('=');
/* 252 */         if (entry.getValue() != null) {
/* 253 */           sb.append(entry.getValue());
/*     */         }
/* 255 */         if (it.hasNext()) {
/* 256 */           sb.append(", ");
/*     */         }
/*     */       } 
/* 259 */       sb.append('}');
/*     */     } else {
/* 261 */       sb.append(this.fields.toString());
/*     */     } 
/*     */     
/* 264 */     sb.append('}');
/* 265 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */