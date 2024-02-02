package cn.hutool.setting;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GroupedMap extends LinkedHashMap<String, LinkedHashMap<String, String>> {
   private static final long serialVersionUID = -7777365130776081931L;
   private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
   private final ReentrantReadWriteLock.ReadLock readLock;
   private final ReentrantReadWriteLock.WriteLock writeLock;
   private int size;

   public GroupedMap() {
      this.readLock = this.cacheLock.readLock();
      this.writeLock = this.cacheLock.writeLock();
      this.size = -1;
   }

   public String get(String group, String key) {
      this.readLock.lock();

      String var4;
      try {
         LinkedHashMap<String, String> map = this.get(StrUtil.nullToEmpty(group));
         if (!MapUtil.isNotEmpty(map)) {
            return null;
         }

         var4 = (String)map.get(key);
      } finally {
         this.readLock.unlock();
      }

      return var4;
   }

   public LinkedHashMap<String, String> get(Object key) {
      this.readLock.lock();

      LinkedHashMap var2;
      try {
         var2 = (LinkedHashMap)super.get(key);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   public int size() {
      this.writeLock.lock();

      try {
         if (this.size < 0) {
            this.size = 0;

            LinkedHashMap value;
            for(Iterator var1 = this.values().iterator(); var1.hasNext(); this.size += value.size()) {
               value = (LinkedHashMap)var1.next();
            }
         }
      } finally {
         this.writeLock.unlock();
      }

      return this.size;
   }

   public String put(String group, String key, String value) {
      group = StrUtil.nullToEmpty(group).trim();
      this.writeLock.lock();

      String var5;
      try {
         LinkedHashMap<String, String> valueMap = (LinkedHashMap)this.computeIfAbsent(group, (k) -> {
            return new LinkedHashMap();
         });
         this.size = -1;
         var5 = (String)valueMap.put(key, value);
      } finally {
         this.writeLock.unlock();
      }

      return var5;
   }

   public GroupedMap putAll(String group, Map<? extends String, ? extends String> m) {
      Iterator var3 = m.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<? extends String, ? extends String> entry = (Map.Entry)var3.next();
         this.put(group, (String)entry.getKey(), (String)entry.getValue());
      }

      return this;
   }

   public String remove(String group, String key) {
      group = StrUtil.nullToEmpty(group).trim();
      this.writeLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            String var4 = (String)valueMap.remove(key);
            return var4;
         }
      } finally {
         this.writeLock.unlock();
      }

      return null;
   }

   public boolean isEmpty(String group) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            boolean var3 = valueMap.isEmpty();
            return var3;
         }
      } finally {
         this.readLock.unlock();
      }

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsKey(String group, String key) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            boolean var4 = valueMap.containsKey(key);
            return var4;
         }
      } finally {
         this.readLock.unlock();
      }

      return false;
   }

   public boolean containsValue(String group, String value) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      boolean var4;
      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (!MapUtil.isNotEmpty(valueMap)) {
            return false;
         }

         var4 = valueMap.containsValue(value);
      } finally {
         this.readLock.unlock();
      }

      return var4;
   }

   public GroupedMap clear(String group) {
      group = StrUtil.nullToEmpty(group).trim();
      this.writeLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            valueMap.clear();
         }
      } finally {
         this.writeLock.unlock();
      }

      return this;
   }

   public Set<String> keySet() {
      this.readLock.lock();

      Set var1;
      try {
         var1 = super.keySet();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Set<String> keySet(String group) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            Set var3 = valueMap.keySet();
            return var3;
         }
      } finally {
         this.readLock.unlock();
      }

      return Collections.emptySet();
   }

   public Collection<String> values(String group) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      Collection var3;
      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (!MapUtil.isNotEmpty(valueMap)) {
            return Collections.emptyList();
         }

         var3 = valueMap.values();
      } finally {
         this.readLock.unlock();
      }

      return var3;
   }

   public Set<Map.Entry<String, LinkedHashMap<String, String>>> entrySet() {
      this.readLock.lock();

      Set var1;
      try {
         var1 = super.entrySet();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Set<Map.Entry<String, String>> entrySet(String group) {
      group = StrUtil.nullToEmpty(group).trim();
      this.readLock.lock();

      try {
         LinkedHashMap<String, String> valueMap = this.get(group);
         if (MapUtil.isNotEmpty(valueMap)) {
            Set var3 = valueMap.entrySet();
            return var3;
         }
      } finally {
         this.readLock.unlock();
      }

      return Collections.emptySet();
   }

   public String toString() {
      this.readLock.lock();

      String var1;
      try {
         var1 = super.toString();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }
}
