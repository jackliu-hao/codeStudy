package org.noear.solon.data.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CacheTags {
   private CacheService _cache;

   public CacheTags(CacheService caching) {
      this._cache = caching;
   }

   public void add(String tag, String targetCacheKey) {
      String tagKey = this._tagKey(tag);
      List<String> cacheKeyList = this._get(tagKey);
      if (!cacheKeyList.contains(targetCacheKey)) {
         cacheKeyList.add(targetCacheKey);
         this._set(tagKey, cacheKeyList);
      }
   }

   public CacheTags remove(String tag) {
      String tagKey = this._tagKey(tag);
      List<String> cacheKeyList = this._get(tagKey);
      Iterator var4 = cacheKeyList.iterator();

      while(var4.hasNext()) {
         String cacheKey = (String)var4.next();
         this._cache.remove(cacheKey);
      }

      this._cache.remove(tagKey);
      return this;
   }

   public void update(String tag, Object newValue, int seconds) {
      String tagKey = this._tagKey(tag);
      List<String> cacheKeyList = this._get(tagKey);
      Iterator var6 = cacheKeyList.iterator();

      while(var6.hasNext()) {
         String cacheKey = (String)var6.next();
         Object temp = this._cache.get(cacheKey);
         if (temp != null) {
            if (newValue == null) {
               this._cache.remove(cacheKey);
            } else if (newValue.getClass() == temp.getClass()) {
               this._cache.store(cacheKey, newValue, seconds);
            }
         }
      }

   }

   protected List<String> _get(String tagKey) {
      Object temp = this._cache.get(tagKey);
      return (List)(temp == null ? new ArrayList() : (List)temp);
   }

   protected void _set(String tagKey, List<String> value) {
      this._cache.store(tagKey, value, 0);
   }

   protected String _tagKey(String tag) {
      return ("@" + tag).toUpperCase();
   }
}
