/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.beans.BeansWrapper;
/*     */ import java.io.Serializable;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
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
/*     */ 
/*     */ public class SimpleHash
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateHashModelEx2, Serializable
/*     */ {
/*     */   private final Map map;
/*     */   private boolean putFailed;
/*     */   private Map unwrappedMap;
/*     */   
/*     */   @Deprecated
/*     */   public SimpleHash() {
/*  86 */     this((ObjectWrapper)null);
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
/*     */   @Deprecated
/*     */   public SimpleHash(Map map) {
/* 102 */     this(map, (ObjectWrapper)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleHash(ObjectWrapper wrapper) {
/* 113 */     super(wrapper);
/* 114 */     this.map = new HashMap<>();
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
/*     */   public SimpleHash(Map<String, Object> directMap, ObjectWrapper wrapper, int overloadDistinction) {
/* 135 */     super(wrapper);
/* 136 */     this.map = directMap;
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
/*     */   public SimpleHash(Map map, ObjectWrapper wrapper) {
/* 152 */     super(wrapper);
/*     */     Map mapCopy;
/*     */     try {
/* 155 */       mapCopy = copyMap(map);
/* 156 */     } catch (ConcurrentModificationException cme) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/* 163 */         Thread.sleep(5L);
/* 164 */       } catch (InterruptedException interruptedException) {}
/*     */       
/* 166 */       synchronized (map) {
/* 167 */         mapCopy = copyMap(map);
/*     */       } 
/*     */     } 
/* 170 */     this.map = mapCopy;
/*     */   }
/*     */   
/*     */   protected Map copyMap(Map<?, ?> map) {
/* 174 */     if (map instanceof HashMap) {
/* 175 */       return (Map)((HashMap)map).clone();
/*     */     }
/* 177 */     if (map instanceof SortedMap) {
/* 178 */       if (map instanceof TreeMap) {
/* 179 */         return (Map)((TreeMap)map).clone();
/*     */       }
/* 181 */       return new TreeMap<>((SortedMap<?, ?>)map);
/*     */     } 
/*     */     
/* 184 */     return new HashMap<>(map);
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
/*     */   public void put(String key, Object value) {
/* 197 */     this.map.put(key, value);
/* 198 */     this.unwrappedMap = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(String key, boolean b) {
/* 209 */     put(key, b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*     */     Object result;
/*     */     try {
/* 216 */       result = this.map.get(key);
/* 217 */     } catch (ClassCastException e) {
/* 218 */       throw new _TemplateModelException(e, new Object[] { "ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key) });
/*     */     
/*     */     }
/* 221 */     catch (NullPointerException e) {
/* 222 */       throw new _TemplateModelException(e, new Object[] { "NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key) });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 231 */     Object putKey = null;
/* 232 */     if (result == null) {
/*     */ 
/*     */       
/* 235 */       if (key.length() == 1 && !(this.map instanceof SortedMap)) {
/* 236 */         Character charKey = Character.valueOf(key.charAt(0));
/*     */         try {
/* 238 */           result = this.map.get(charKey);
/* 239 */           if (result != null || this.map.containsKey(charKey)) {
/* 240 */             putKey = charKey;
/*     */           }
/* 242 */         } catch (ClassCastException e) {
/* 243 */           throw new _TemplateModelException(e, new Object[] { "ClassCastException while getting Map entry with Character key ", new _DelayedJQuote(key) });
/*     */         
/*     */         }
/* 246 */         catch (NullPointerException e) {
/* 247 */           throw new _TemplateModelException(e, new Object[] { "NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(key) });
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 252 */       if (putKey == null) {
/* 253 */         if (!this.map.containsKey(key)) {
/* 254 */           return null;
/*     */         }
/* 256 */         putKey = key;
/*     */       } 
/*     */     } else {
/*     */       
/* 260 */       putKey = key;
/*     */     } 
/*     */     
/* 263 */     if (result instanceof TemplateModel) {
/* 264 */       return (TemplateModel)result;
/*     */     }
/*     */     
/* 267 */     TemplateModel tm = wrap(result);
/* 268 */     if (!this.putFailed) {
/*     */       try {
/* 270 */         this.map.put(putKey, tm);
/* 271 */       } catch (Exception e) {
/*     */         
/* 273 */         this.putFailed = true;
/*     */       } 
/*     */     }
/* 276 */     return tm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(String key) {
/* 284 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String key) {
/* 293 */     this.map.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAll(Map m) {
/* 302 */     for (Iterator<Map.Entry> it = m.entrySet().iterator(); it.hasNext(); ) {
/* 303 */       Map.Entry entry = it.next();
/* 304 */       put((String)entry.getKey(), entry.getValue());
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
/*     */   public Map toMap() throws TemplateModelException {
/* 316 */     if (this.unwrappedMap == null) {
/* 317 */       Class<?> mapClass = this.map.getClass();
/* 318 */       Map<Object, Object> m = null;
/*     */       try {
/* 320 */         m = (Map)mapClass.newInstance();
/* 321 */       } catch (Exception e) {
/* 322 */         throw new TemplateModelException("Error instantiating map of type " + mapClass.getName() + "\n" + e.getMessage());
/*     */       } 
/*     */ 
/*     */       
/* 326 */       BeansWrapper bw = BeansWrapper.getDefaultInstance();
/* 327 */       for (Iterator<Map.Entry> it = this.map.entrySet().iterator(); it.hasNext(); ) {
/* 328 */         Map.Entry entry = it.next();
/* 329 */         Object key = entry.getKey();
/* 330 */         Object value = entry.getValue();
/* 331 */         if (value instanceof TemplateModel) {
/* 332 */           value = bw.unwrap((TemplateModel)value);
/*     */         }
/* 334 */         m.put(key, value);
/*     */       } 
/* 336 */       this.unwrappedMap = m;
/*     */     } 
/* 338 */     return this.unwrappedMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 346 */     return this.map.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 351 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 356 */     return (this.map == null || this.map.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/* 361 */     return new SimpleCollection(this.map.keySet(), getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() {
/* 366 */     return new SimpleCollection(this.map.values(), getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
/* 371 */     return new MapKeyValuePairIterator(this.map, getObjectWrapper());
/*     */   }
/*     */   
/*     */   public SimpleHash synchronizedWrapper() {
/* 375 */     return new SynchronizedHash();
/*     */   }
/*     */   
/*     */   private class SynchronizedHash extends SimpleHash {
/*     */     private SynchronizedHash() {}
/*     */     
/*     */     public boolean isEmpty() {
/* 382 */       synchronized (SimpleHash.this) {
/* 383 */         return SimpleHash.this.isEmpty();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void put(String key, Object obj) {
/* 389 */       synchronized (SimpleHash.this) {
/* 390 */         SimpleHash.this.put(key, obj);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 396 */       synchronized (SimpleHash.this) {
/* 397 */         return SimpleHash.this.get(key);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove(String key) {
/* 403 */       synchronized (SimpleHash.this) {
/* 404 */         SimpleHash.this.remove(key);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 410 */       synchronized (SimpleHash.this) {
/* 411 */         return SimpleHash.this.size();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel keys() {
/* 417 */       synchronized (SimpleHash.this) {
/* 418 */         return SimpleHash.this.keys();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() {
/* 424 */       synchronized (SimpleHash.this) {
/* 425 */         return SimpleHash.this.values();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
/* 431 */       synchronized (SimpleHash.this) {
/* 432 */         return SimpleHash.this.keyValuePairIterator();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Map toMap() throws TemplateModelException {
/* 438 */       synchronized (SimpleHash.this) {
/* 439 */         return SimpleHash.this.toMap();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleHash.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */