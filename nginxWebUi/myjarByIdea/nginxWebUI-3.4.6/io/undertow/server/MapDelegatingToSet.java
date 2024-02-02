package io.undertow.server;

import io.undertow.server.handlers.Cookie;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

final class MapDelegatingToSet extends HashMap<String, Cookie> {
   private final Set<Cookie> delegate;

   MapDelegatingToSet(Set<Cookie> delegate) {
      this.delegate = delegate;
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public Cookie get(Object key) {
      if (key == null) {
         return null;
      } else {
         Iterator var2 = this.delegate.iterator();

         Cookie cookie;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            cookie = (Cookie)var2.next();
         } while(!key.equals(cookie.getName()));

         return cookie;
      }
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return false;
      } else {
         Iterator var2 = this.delegate.iterator();

         Cookie cookie;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            cookie = (Cookie)var2.next();
         } while(!key.equals(cookie.getName()));

         return true;
      }
   }

   public Cookie put(String key, Cookie value) {
      if (key == null) {
         return null;
      } else {
         Cookie retVal = this.remove(key);
         if (value != null) {
            this.delegate.add(value);
         }

         return retVal;
      }
   }

   public void putAll(Map<? extends String, ? extends Cookie> m) {
      if (m != null) {
         Iterator var2 = m.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<? extends String, ? extends Cookie> entry = (Map.Entry)var2.next();
            this.put((String)entry.getKey(), (Cookie)entry.getValue());
         }

      }
   }

   public Cookie remove(Object key) {
      if (key == null) {
         return null;
      } else {
         Cookie removedValue = null;
         Iterator var3 = this.delegate.iterator();

         while(var3.hasNext()) {
            Cookie cookie = (Cookie)var3.next();
            if (key.equals(cookie.getName())) {
               removedValue = cookie;
               break;
            }
         }

         if (removedValue != null) {
            this.delegate.remove(removedValue);
         }

         return removedValue;
      }
   }

   public void clear() {
      this.delegate.clear();
   }

   public boolean containsValue(Object value) {
      return value == null ? false : this.delegate.contains(value);
   }

   public Set<String> keySet() {
      if (this.delegate.isEmpty()) {
         return Collections.emptySet();
      } else {
         Set<String> retVal = new HashSet();
         Iterator var2 = this.delegate.iterator();

         while(var2.hasNext()) {
            Cookie cookie = (Cookie)var2.next();
            retVal.add(cookie.getName());
         }

         return Collections.unmodifiableSet(retVal);
      }
   }

   public Collection<Cookie> values() {
      return (Collection)(this.delegate.isEmpty() ? Collections.emptySet() : Collections.unmodifiableCollection(this.delegate));
   }

   public Set<Map.Entry<String, Cookie>> entrySet() {
      if (this.delegate.isEmpty()) {
         return Collections.emptySet();
      } else {
         Set<Map.Entry<String, Cookie>> retVal = new HashSet(this.delegate.size());
         Iterator var2 = this.delegate.iterator();

         while(var2.hasNext()) {
            Cookie cookie = (Cookie)var2.next();
            retVal.add(new ReadOnlyEntry(cookie.getName(), cookie));
         }

         return Collections.unmodifiableSet(retVal);
      }
   }

   public Cookie getOrDefault(Object key, Cookie defaultValue) {
      if (key == null) {
         return null;
      } else {
         Cookie retVal = this.get(key);
         return retVal != null ? retVal : defaultValue;
      }
   }

   public Cookie putIfAbsent(String key, Cookie value) {
      if (key == null) {
         return null;
      } else {
         Cookie oldVal = this.get(key);
         if (oldVal == null) {
            this.delegate.add(value);
         }

         return oldVal;
      }
   }

   public boolean remove(Object key, Object value) {
      if (key != null && value != null) {
         Cookie removedValue = null;
         Iterator var4 = this.delegate.iterator();

         while(var4.hasNext()) {
            Cookie cookie = (Cookie)var4.next();
            if (cookie == value) {
               removedValue = cookie;
               break;
            }
         }

         if (removedValue != null) {
            this.delegate.remove(removedValue);
         }

         return removedValue != null;
      } else {
         return false;
      }
   }

   public boolean replace(String key, Cookie oldValue, Cookie newValue) {
      if (key == null) {
         return false;
      } else {
         Cookie previousValue = this.get(key);
         if (previousValue == oldValue) {
            this.delegate.remove(oldValue);
            if (newValue != null) {
               this.delegate.add(newValue);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public Cookie replace(String key, Cookie value) {
      if (key == null) {
         return null;
      } else {
         Cookie oldValue = this.get(key);
         if (oldValue != null) {
            this.delegate.remove(oldValue);
            if (value != null) {
               this.delegate.add(value);
            }
         }

         return oldValue;
      }
   }

   public Cookie computeIfAbsent(String key, Function<? super String, ? extends Cookie> mappingFunction) {
      throw new UnsupportedOperationException();
   }

   public Cookie computeIfPresent(String key, BiFunction<? super String, ? super Cookie, ? extends Cookie> remappingFunction) {
      throw new UnsupportedOperationException();
   }

   public Cookie compute(String key, BiFunction<? super String, ? super Cookie, ? extends Cookie> remappingFunction) {
      throw new UnsupportedOperationException();
   }

   public Cookie merge(String key, Cookie value, BiFunction<? super Cookie, ? super Cookie, ? extends Cookie> remappingFunction) {
      throw new UnsupportedOperationException();
   }

   public void forEach(BiConsumer<? super String, ? super Cookie> action) {
      throw new UnsupportedOperationException();
   }

   public void replaceAll(BiFunction<? super String, ? super Cookie, ? extends Cookie> function) {
      throw new UnsupportedOperationException();
   }

   private static final class ReadOnlyEntry implements Map.Entry<String, Cookie> {
      private final String key;
      private final Cookie value;

      private ReadOnlyEntry(String key, Cookie value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return this.key;
      }

      public Cookie getValue() {
         return this.value;
      }

      public Cookie setValue(Cookie cookie) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      ReadOnlyEntry(String x0, Cookie x1, Object x2) {
         this(x0, x1);
      }
   }
}
