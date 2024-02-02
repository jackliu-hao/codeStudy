package freemarker.template;

import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SimpleHash extends WrappingTemplateModel implements TemplateHashModelEx2, Serializable {
   private final Map map;
   private boolean putFailed;
   private Map unwrappedMap;

   /** @deprecated */
   @Deprecated
   public SimpleHash() {
      this((ObjectWrapper)null);
   }

   /** @deprecated */
   @Deprecated
   public SimpleHash(Map map) {
      this(map, (ObjectWrapper)null);
   }

   public SimpleHash(ObjectWrapper wrapper) {
      super(wrapper);
      this.map = new HashMap();
   }

   public SimpleHash(Map<String, Object> directMap, ObjectWrapper wrapper, int overloadDistinction) {
      super(wrapper);
      this.map = directMap;
   }

   public SimpleHash(Map map, ObjectWrapper wrapper) {
      super(wrapper);

      Map mapCopy;
      try {
         mapCopy = this.copyMap(map);
      } catch (ConcurrentModificationException var9) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var8) {
         }

         synchronized(map) {
            mapCopy = this.copyMap(map);
         }
      }

      this.map = mapCopy;
   }

   protected Map copyMap(Map map) {
      if (map instanceof HashMap) {
         return (Map)((HashMap)map).clone();
      } else if (map instanceof SortedMap) {
         return (Map)(map instanceof TreeMap ? (Map)((TreeMap)map).clone() : new TreeMap((SortedMap)map));
      } else {
         return new HashMap(map);
      }
   }

   public void put(String key, Object value) {
      this.map.put(key, value);
      this.unwrappedMap = null;
   }

   public void put(String key, boolean b) {
      this.put(key, b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
   }

   public TemplateModel get(String key) throws TemplateModelException {
      Object result;
      try {
         result = this.map.get(key);
      } catch (ClassCastException var7) {
         throw new _TemplateModelException(var7, new Object[]{"ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key)});
      } catch (NullPointerException var8) {
         throw new _TemplateModelException(var8, new Object[]{"NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key)});
      }

      Object putKey = null;
      if (result == null) {
         if (key.length() == 1 && !(this.map instanceof SortedMap)) {
            Character charKey = key.charAt(0);

            try {
               result = this.map.get(charKey);
               if (result != null || this.map.containsKey(charKey)) {
                  putKey = charKey;
               }
            } catch (ClassCastException var9) {
               throw new _TemplateModelException(var9, new Object[]{"ClassCastException while getting Map entry with Character key ", new _DelayedJQuote(key)});
            } catch (NullPointerException var10) {
               throw new _TemplateModelException(var10, new Object[]{"NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(key)});
            }
         }

         if (putKey == null) {
            if (!this.map.containsKey(key)) {
               return null;
            }

            putKey = key;
         }
      } else {
         putKey = key;
      }

      if (result instanceof TemplateModel) {
         return (TemplateModel)result;
      } else {
         TemplateModel tm = this.wrap(result);
         if (!this.putFailed) {
            try {
               this.map.put(putKey, tm);
            } catch (Exception var6) {
               this.putFailed = true;
            }
         }

         return tm;
      }
   }

   public boolean containsKey(String key) {
      return this.map.containsKey(key);
   }

   public void remove(String key) {
      this.map.remove(key);
   }

   public void putAll(Map m) {
      Iterator it = m.entrySet().iterator();

      while(it.hasNext()) {
         Map.Entry entry = (Map.Entry)it.next();
         this.put((String)entry.getKey(), entry.getValue());
      }

   }

   public Map toMap() throws TemplateModelException {
      if (this.unwrappedMap == null) {
         Class mapClass = this.map.getClass();
         Map m = null;

         try {
            m = (Map)mapClass.newInstance();
         } catch (Exception var8) {
            throw new TemplateModelException("Error instantiating map of type " + mapClass.getName() + "\n" + var8.getMessage());
         }

         BeansWrapper bw = BeansWrapper.getDefaultInstance();

         Object key;
         Object value;
         for(Iterator it = this.map.entrySet().iterator(); it.hasNext(); m.put(key, value)) {
            Map.Entry entry = (Map.Entry)it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (value instanceof TemplateModel) {
               value = bw.unwrap((TemplateModel)value);
            }
         }

         this.unwrappedMap = m;
      }

      return this.unwrappedMap;
   }

   public String toString() {
      return this.map.toString();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map == null || this.map.isEmpty();
   }

   public TemplateCollectionModel keys() {
      return new SimpleCollection(this.map.keySet(), this.getObjectWrapper());
   }

   public TemplateCollectionModel values() {
      return new SimpleCollection(this.map.values(), this.getObjectWrapper());
   }

   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
      return new MapKeyValuePairIterator(this.map, this.getObjectWrapper());
   }

   public SimpleHash synchronizedWrapper() {
      return new SynchronizedHash();
   }

   private class SynchronizedHash extends SimpleHash {
      private SynchronizedHash() {
      }

      public boolean isEmpty() {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.isEmpty();
         }
      }

      public void put(String key, Object obj) {
         synchronized(SimpleHash.this) {
            SimpleHash.this.put(key, obj);
         }
      }

      public TemplateModel get(String key) throws TemplateModelException {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.get(key);
         }
      }

      public void remove(String key) {
         synchronized(SimpleHash.this) {
            SimpleHash.this.remove(key);
         }
      }

      public int size() {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.size();
         }
      }

      public TemplateCollectionModel keys() {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.keys();
         }
      }

      public TemplateCollectionModel values() {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.values();
         }
      }

      public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.keyValuePairIterator();
         }
      }

      public Map toMap() throws TemplateModelException {
         synchronized(SimpleHash.this) {
            return SimpleHash.this.toMap();
         }
      }

      // $FF: synthetic method
      SynchronizedHash(Object x1) {
         this();
      }
   }
}
