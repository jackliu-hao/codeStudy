/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
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
/*     */ final class MapDelegatingToSet
/*     */   extends HashMap<String, Cookie>
/*     */ {
/*     */   private final Set<Cookie> delegate;
/*     */   
/*     */   MapDelegatingToSet(Set<Cookie> delegate) {
/*  43 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  48 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  53 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie get(Object key) {
/*  58 */     if (key == null) return null; 
/*  59 */     for (Cookie cookie : this.delegate) {
/*  60 */       if (key.equals(cookie.getName())) return cookie; 
/*     */     } 
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  67 */     if (key == null) return false; 
/*  68 */     for (Cookie cookie : this.delegate) {
/*  69 */       if (key.equals(cookie.getName())) return true; 
/*     */     } 
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie put(String key, Cookie value) {
/*  76 */     if (key == null) return null; 
/*  77 */     Cookie retVal = remove(key);
/*  78 */     if (value != null) {
/*  79 */       this.delegate.add(value);
/*     */     }
/*  81 */     return retVal;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends Cookie> m) {
/*  86 */     if (m == null)
/*  87 */       return;  for (Map.Entry<? extends String, ? extends Cookie> entry : m.entrySet()) {
/*  88 */       put(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie remove(Object key) {
/*  94 */     if (key == null) return null; 
/*  95 */     Cookie removedValue = null;
/*  96 */     for (Cookie cookie : this.delegate) {
/*  97 */       if (key.equals(cookie.getName())) {
/*  98 */         removedValue = cookie;
/*     */         break;
/*     */       } 
/*     */     } 
/* 102 */     if (removedValue != null) this.delegate.remove(removedValue); 
/* 103 */     return removedValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 108 */     this.delegate.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 113 */     if (value == null) return false; 
/* 114 */     return this.delegate.contains(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 119 */     if (this.delegate.isEmpty()) return Collections.emptySet(); 
/* 120 */     Set<String> retVal = new HashSet<>();
/* 121 */     for (Cookie cookie : this.delegate) {
/* 122 */       retVal.add(cookie.getName());
/*     */     }
/* 124 */     return Collections.unmodifiableSet(retVal);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<Cookie> values() {
/* 129 */     return this.delegate.isEmpty() ? Collections.<Cookie>emptySet() : Collections.<Cookie>unmodifiableCollection(this.delegate);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, Cookie>> entrySet() {
/* 134 */     if (this.delegate.isEmpty()) return Collections.emptySet(); 
/* 135 */     Set<Map.Entry<String, Cookie>> retVal = new HashSet<>(this.delegate.size());
/* 136 */     for (Cookie cookie : this.delegate) {
/* 137 */       retVal.add(new ReadOnlyEntry(cookie.getName(), cookie));
/*     */     }
/* 139 */     return Collections.unmodifiableSet(retVal);
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie getOrDefault(Object key, Cookie defaultValue) {
/* 144 */     if (key == null) return null; 
/* 145 */     Cookie retVal = get(key);
/* 146 */     return (retVal != null) ? retVal : defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie putIfAbsent(String key, Cookie value) {
/* 151 */     if (key == null) return null; 
/* 152 */     Cookie oldVal = get(key);
/* 153 */     if (oldVal == null) this.delegate.add(value); 
/* 154 */     return oldVal;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object key, Object value) {
/* 159 */     if (key == null || value == null) return false; 
/* 160 */     Cookie removedValue = null;
/* 161 */     for (Cookie cookie : this.delegate) {
/* 162 */       if (cookie == value) {
/* 163 */         removedValue = cookie;
/*     */         break;
/*     */       } 
/*     */     } 
/* 167 */     if (removedValue != null) this.delegate.remove(removedValue); 
/* 168 */     return (removedValue != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean replace(String key, Cookie oldValue, Cookie newValue) {
/* 173 */     if (key == null) return false; 
/* 174 */     Cookie previousValue = get(key);
/* 175 */     if (previousValue == oldValue) {
/* 176 */       this.delegate.remove(oldValue);
/* 177 */       if (newValue != null) {
/* 178 */         this.delegate.add(newValue);
/*     */       }
/* 180 */       return true;
/*     */     } 
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie replace(String key, Cookie value) {
/* 187 */     if (key == null) return null; 
/* 188 */     Cookie oldValue = get(key);
/* 189 */     if (oldValue != null) {
/* 190 */       this.delegate.remove(oldValue);
/* 191 */       if (value != null) {
/* 192 */         this.delegate.add(value);
/*     */       }
/*     */     } 
/* 195 */     return oldValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie computeIfAbsent(String key, Function<? super String, ? extends Cookie> mappingFunction) {
/* 200 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie computeIfPresent(String key, BiFunction<? super String, ? super Cookie, ? extends Cookie> remappingFunction) {
/* 205 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie compute(String key, BiFunction<? super String, ? super Cookie, ? extends Cookie> remappingFunction) {
/* 210 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie merge(String key, Cookie value, BiFunction<? super Cookie, ? super Cookie, ? extends Cookie> remappingFunction) {
/* 215 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEach(BiConsumer<? super String, ? super Cookie> action) {
/* 220 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void replaceAll(BiFunction<? super String, ? super Cookie, ? extends Cookie> function) {
/* 225 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   private static final class ReadOnlyEntry implements Map.Entry<String, Cookie> {
/*     */     private final String key;
/*     */     private final Cookie value;
/*     */     
/*     */     private ReadOnlyEntry(String key, Cookie value) {
/* 233 */       this.key = key;
/* 234 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 239 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public Cookie getValue() {
/* 244 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public Cookie setValue(Cookie cookie) {
/* 249 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\MapDelegatingToSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */