package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionUtils {
   public static Map mergeMaps(Map dominantMap, Map recessiveMap) {
      if (dominantMap == null && recessiveMap == null) {
         return null;
      } else if (dominantMap != null && recessiveMap == null) {
         return dominantMap;
      } else if (dominantMap == null && recessiveMap != null) {
         return recessiveMap;
      } else {
         Map result = new HashMap();
         Set dominantMapKeys = dominantMap.keySet();
         Set recessiveMapKeys = recessiveMap.keySet();
         Collection contributingRecessiveKeys = subtract(recessiveMapKeys, intersection(dominantMapKeys, recessiveMapKeys));
         result.putAll(dominantMap);
         Iterator i = contributingRecessiveKeys.iterator();

         while(i.hasNext()) {
            Object key = i.next();
            result.put(key, recessiveMap.get(key));
         }

         return result;
      }
   }

   public static Map mergeMaps(Map[] maps) {
      Map result = null;
      if (maps.length == 0) {
         result = null;
      } else if (maps.length == 1) {
         result = maps[0];
      } else {
         result = mergeMaps(maps[0], maps[1]);

         for(int i = 2; i < maps.length; ++i) {
            result = mergeMaps(result, maps[i]);
         }
      }

      return result;
   }

   public static Collection intersection(Collection a, Collection b) {
      ArrayList list = new ArrayList();
      Map mapa = getCardinalityMap(a);
      Map mapb = getCardinalityMap(b);
      Set elts = new HashSet(a);
      elts.addAll(b);
      Iterator it = elts.iterator();

      while(it.hasNext()) {
         Object obj = it.next();
         int i = 0;

         for(int m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
            list.add(obj);
         }
      }

      return list;
   }

   public static Collection subtract(Collection a, Collection b) {
      ArrayList list = new ArrayList(a);
      Iterator it = b.iterator();

      while(it.hasNext()) {
         list.remove(it.next());
      }

      return list;
   }

   public static Map getCardinalityMap(Collection col) {
      HashMap count = new HashMap();
      Iterator it = col.iterator();

      while(it.hasNext()) {
         Object obj = it.next();
         Integer c = (Integer)count.get(obj);
         if (null == c) {
            count.put(obj, new Integer(1));
         } else {
            count.put(obj, new Integer(c + 1));
         }
      }

      return count;
   }

   public static List iteratorToList(Iterator it) {
      if (it == null) {
         throw new NullPointerException("it cannot be null.");
      } else {
         List list = new ArrayList();

         while(it.hasNext()) {
            list.add(it.next());
         }

         return list;
      }
   }

   private static final int getFreq(Object obj, Map freqMap) {
      try {
         Object o = freqMap.get(obj);
         if (o != null) {
            return (Integer)o;
         }
      } catch (NullPointerException var3) {
      } catch (NoSuchElementException var4) {
      }

      return 0;
   }
}
