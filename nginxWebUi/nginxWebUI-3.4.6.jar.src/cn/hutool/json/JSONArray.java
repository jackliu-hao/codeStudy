/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanPath;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutableObj;
/*     */ import cn.hutool.core.lang.mutable.MutablePair;
/*     */ import cn.hutool.core.text.StrJoiner;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.json.serialize.JSONWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.RandomAccess;
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
/*     */ public class JSONArray
/*     */   implements JSON, JSONGetter<Integer>, List<Object>, RandomAccess
/*     */ {
/*     */   private static final long serialVersionUID = 2664900568717612292L;
/*     */   public static final int DEFAULT_CAPACITY = 10;
/*     */   private List<Object> rawList;
/*     */   private final JSONConfig config;
/*     */   
/*     */   public JSONArray() {
/*  58 */     this(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONArray(int initialCapacity) {
/*  69 */     this(initialCapacity, JSONConfig.create());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONArray(JSONConfig config) {
/*  80 */     this(10, config);
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
/*     */   public JSONArray(int initialCapacity, JSONConfig config) {
/*  92 */     this.rawList = new ArrayList(initialCapacity);
/*  93 */     this.config = (JSONConfig)ObjectUtil.defaultIfNull(config, JSONConfig::create);
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
/*     */   public JSONArray(Object object) throws JSONException {
/* 110 */     this(object, true);
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
/*     */   public JSONArray(Object object, boolean ignoreNullValue) throws JSONException {
/* 128 */     this(object, JSONConfig.create().setIgnoreNullValue(ignoreNullValue));
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
/*     */   public JSONArray(Object object, JSONConfig jsonConfig) throws JSONException {
/* 147 */     this(object, jsonConfig, (Filter<Mutable<Object>>)null);
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
/*     */   public JSONArray(Object object, JSONConfig jsonConfig, Filter<Mutable<Object>> filter) throws JSONException {
/* 167 */     this(10, jsonConfig);
/* 168 */     ObjectMapper.of(object).map(this, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig getConfig() {
/* 174 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONArray setDateFormat(String format) {
/* 185 */     this.config.setDateFormat(format);
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String join(String separator) throws JSONException {
/* 197 */     return StrJoiner.of(separator)
/* 198 */       .append(this, InternalJSONUtil::valueToString).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(int index) {
/* 203 */     return this.rawList.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getObj(Integer index, Object defaultValue) {
/* 208 */     return (index.intValue() < 0 || index.intValue() >= size()) ? defaultValue : this.rawList.get(index.intValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getByPath(String expression) {
/* 213 */     return BeanPath.create(expression).get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getByPath(String expression, Class<T> resultType) {
/* 218 */     return JSONConverter.jsonConvert(resultType, getByPath(expression), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putByPath(String expression, Object value) {
/* 223 */     BeanPath.create(expression).set(this, value);
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
/*     */   public JSONArray put(Object value) {
/* 235 */     return set(value);
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
/*     */   public JSONArray set(Object value) {
/* 247 */     add(value);
/* 248 */     return this;
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
/*     */   public JSONArray put(int index, Object value) throws JSONException {
/* 261 */     set(index, value);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T toBean(Type type) {
/* 267 */     return toBean(type, this.config.isIgnoreError());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject toJSONObject(JSONArray names) throws JSONException {
/* 278 */     if (names == null || names.size() == 0 || size() == 0) {
/* 279 */       return null;
/*     */     }
/* 281 */     JSONObject jo = new JSONObject(this.config);
/* 282 */     for (int i = 0; i < names.size(); i++) {
/* 283 */       jo.set(names.getStr(Integer.valueOf(i)), getObj(Integer.valueOf(i)));
/*     */     }
/* 285 */     return jo;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 290 */     int prime = 31;
/* 291 */     int result = 1;
/* 292 */     result = 31 * result + ((this.rawList == null) ? 0 : this.rawList.hashCode());
/* 293 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 298 */     if (this == obj) {
/* 299 */       return true;
/*     */     }
/* 301 */     if (obj == null) {
/* 302 */       return false;
/*     */     }
/* 304 */     if (getClass() != obj.getClass()) {
/* 305 */       return false;
/*     */     }
/* 307 */     JSONArray other = (JSONArray)obj;
/* 308 */     if (this.rawList == null) {
/* 309 */       return (other.rawList == null);
/*     */     }
/* 311 */     return this.rawList.equals(other.rawList);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Object> iterator() {
/* 317 */     return this.rawList.iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<JSONObject> jsonIter() {
/* 327 */     return new JSONObjectIter(iterator());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 332 */     return this.rawList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 337 */     return this.rawList.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 342 */     return this.rawList.contains(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 347 */     return this.rawList.toArray();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/* 353 */     return (T[])JSONConverter.toArray(this, a.getClass().getComponentType());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Object e) {
/* 358 */     return addRaw(JSONUtil.wrap(e, this.config), (Filter<Mutable<Object>>)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object remove(int index) {
/* 363 */     return (index >= 0 && index < size()) ? this.rawList.remove(index) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 368 */     return this.rawList.remove(o);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/* 374 */     return this.rawList.containsAll(c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<?> c) {
/* 380 */     if (CollUtil.isEmpty(c)) {
/* 381 */       return false;
/*     */     }
/* 383 */     for (Object obj : c) {
/* 384 */       add(obj);
/*     */     }
/* 386 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<?> c) {
/* 392 */     if (CollUtil.isEmpty(c)) {
/* 393 */       return false;
/*     */     }
/* 395 */     ArrayList<Object> list = new ArrayList(c.size());
/* 396 */     for (Object object : c) {
/* 397 */       list.add(JSONUtil.wrap(object, this.config));
/*     */     }
/* 399 */     return this.rawList.addAll(index, list);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 405 */     return this.rawList.removeAll(c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 411 */     return this.rawList.retainAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 416 */     this.rawList.clear();
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
/*     */   public Object set(int index, Object element) {
/* 429 */     return set(index, element, (Filter<MutablePair<Integer, Object>>)null);
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
/*     */   public Object set(int index, Object element, Filter<MutablePair<Integer, Object>> filter) {
/* 443 */     if (null != filter) {
/* 444 */       MutablePair<Integer, Object> pair = new MutablePair(Integer.valueOf(index), element);
/* 445 */       if (filter.accept(pair))
/*     */       {
/* 447 */         element = pair.getValue();
/*     */       }
/*     */     } 
/*     */     
/* 451 */     if (index >= size()) {
/* 452 */       add(index, element);
/*     */     }
/* 454 */     return this.rawList.set(index, JSONUtil.wrap(element, this.config));
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Object element) {
/* 459 */     if (index < 0) {
/* 460 */       throw new JSONException("JSONArray[{}] not found.", new Object[] { Integer.valueOf(index) });
/*     */     }
/* 462 */     if (index < size()) {
/* 463 */       InternalJSONUtil.testValidity(element);
/* 464 */       this.rawList.add(index, JSONUtil.wrap(element, this.config));
/*     */     } else {
/* 466 */       while (index != size()) {
/* 467 */         add(JSONNull.NULL);
/*     */       }
/* 469 */       set(element);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int indexOf(Object o) {
/* 476 */     return this.rawList.indexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 481 */     return this.rawList.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<Object> listIterator() {
/* 486 */     return this.rawList.listIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<Object> listIterator(int index) {
/* 491 */     return this.rawList.listIterator(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Object> subList(int fromIndex, int toIndex) {
/* 496 */     return this.rawList.subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object toArray(Class<?> arrayClass) {
/* 506 */     return JSONConverter.toArray(this, arrayClass);
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
/*     */   public <T> List<T> toList(Class<T> elementType) {
/* 518 */     return JSONConverter.toList(this, elementType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 528 */     return toJSONString(0);
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
/*     */   public String toJSONString(int indentFactor, Filter<MutablePair<Integer, Object>> filter) {
/* 541 */     StringWriter sw = new StringWriter();
/* 542 */     synchronized (sw.getBuffer()) {
/* 543 */       return write(sw, indentFactor, 0, filter).toString();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
/* 549 */     return write(writer, indentFactor, indent, (Filter<MutablePair<Integer, Object>>)null);
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
/*     */   public Writer write(Writer writer, int indentFactor, int indent, Filter<MutablePair<Integer, Object>> filter) throws JSONException {
/* 566 */     JSONWriter jsonWriter = JSONWriter.of(writer, indentFactor, indent, this.config).beginArray();
/*     */     
/* 568 */     CollUtil.forEach(this, (value, index) -> {
/*     */           MutablePair<Integer, Object> pair = new MutablePair(Integer.valueOf(index), value);
/*     */           if (null == filter || filter.accept(pair)) {
/*     */             jsonWriter.writeValue(pair.getValue());
/*     */           }
/*     */         });
/* 574 */     jsonWriter.end();
/*     */     
/* 576 */     return writer;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 581 */     JSONArray clone = (JSONArray)super.clone();
/* 582 */     clone.rawList = (List<Object>)ObjectUtil.clone(this.rawList);
/* 583 */     return clone;
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
/*     */   protected boolean addRaw(Object obj, Filter<Mutable<Object>> filter) {
/* 596 */     if (null != filter) {
/* 597 */       MutableObj mutableObj = new MutableObj(obj);
/* 598 */       if (filter.accept(mutableObj)) {
/*     */         
/* 600 */         obj = mutableObj.get();
/*     */       } else {
/*     */         
/* 603 */         return false;
/*     */       } 
/*     */     } 
/* 606 */     return this.rawList.add(obj);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */