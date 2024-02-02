/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
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
/*     */ public class CollectionUtils
/*     */ {
/*     */   public static Map mergeMaps(Map dominantMap, Map recessiveMap) {
/*  61 */     if (dominantMap == null && recessiveMap == null)
/*     */     {
/*  63 */       return null;
/*     */     }
/*     */     
/*  66 */     if (dominantMap != null && recessiveMap == null)
/*     */     {
/*  68 */       return dominantMap;
/*     */     }
/*     */     
/*  71 */     if (dominantMap == null && recessiveMap != null)
/*     */     {
/*  73 */       return recessiveMap;
/*     */     }
/*     */     
/*  76 */     Map result = new HashMap();
/*     */ 
/*     */     
/*  79 */     Set dominantMapKeys = dominantMap.keySet();
/*  80 */     Set recessiveMapKeys = recessiveMap.keySet();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     Collection contributingRecessiveKeys = subtract(recessiveMapKeys, intersection(dominantMapKeys, recessiveMapKeys));
/*     */ 
/*     */ 
/*     */     
/*  89 */     result.putAll(dominantMap);
/*     */ 
/*     */ 
/*     */     
/*  93 */     for (Iterator i = contributingRecessiveKeys.iterator(); i.hasNext(); ) {
/*     */       
/*  95 */       Object key = i.next();
/*  96 */       result.put(key, recessiveMap.get(key));
/*     */     } 
/*     */     
/*  99 */     return result;
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
/*     */   public static Map mergeMaps(Map[] maps) {
/* 112 */     Map result = null;
/*     */     
/* 114 */     if (maps.length == 0) {
/*     */       
/* 116 */       result = null;
/*     */     }
/* 118 */     else if (maps.length == 1) {
/*     */       
/* 120 */       result = maps[0];
/*     */     }
/*     */     else {
/*     */       
/* 124 */       result = mergeMaps(maps[0], maps[1]);
/*     */       
/* 126 */       for (int i = 2; i < maps.length; i++)
/*     */       {
/* 128 */         result = mergeMaps(result, maps[i]);
/*     */       }
/*     */     } 
/*     */     
/* 132 */     return result;
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
/*     */   public static Collection intersection(Collection a, Collection b) {
/* 147 */     ArrayList list = new ArrayList();
/* 148 */     Map mapa = getCardinalityMap(a);
/* 149 */     Map mapb = getCardinalityMap(b);
/* 150 */     Set elts = new HashSet(a);
/* 151 */     elts.addAll(b);
/* 152 */     Iterator it = elts.iterator();
/* 153 */     while (it.hasNext()) {
/*     */       
/* 155 */       Object obj = it.next();
/* 156 */       for (int i = 0, m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; i++)
/*     */       {
/* 158 */         list.add(obj);
/*     */       }
/*     */     } 
/* 161 */     return list;
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
/*     */   public static Collection subtract(Collection a, Collection b) {
/* 174 */     ArrayList list = new ArrayList(a);
/* 175 */     Iterator it = b.iterator();
/* 176 */     while (it.hasNext())
/*     */     {
/* 178 */       list.remove(it.next());
/*     */     }
/* 180 */     return list;
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
/*     */   public static Map getCardinalityMap(Collection col) {
/* 193 */     HashMap count = new HashMap();
/* 194 */     Iterator it = col.iterator();
/* 195 */     while (it.hasNext()) {
/*     */       
/* 197 */       Object obj = it.next();
/* 198 */       Integer c = (Integer)count.get(obj);
/* 199 */       if (null == c) {
/*     */         
/* 201 */         count.put(obj, new Integer(1));
/*     */         
/*     */         continue;
/*     */       } 
/* 205 */       count.put(obj, new Integer(c.intValue() + 1));
/*     */     } 
/*     */     
/* 208 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List iteratorToList(Iterator it) {
/* 213 */     if (it == null)
/*     */     {
/* 215 */       throw new NullPointerException("it cannot be null.");
/*     */     }
/*     */     
/* 218 */     List list = new ArrayList();
/*     */     
/* 220 */     while (it.hasNext())
/*     */     {
/* 222 */       list.add(it.next());
/*     */     }
/*     */     
/* 225 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int getFreq(Object obj, Map freqMap) {
/*     */     try {
/* 236 */       Object o = freqMap.get(obj);
/* 237 */       if (o != null)
/*     */       {
/* 239 */         return ((Integer)o).intValue();
/*     */       }
/*     */     }
/* 242 */     catch (NullPointerException e) {
/*     */ 
/*     */     
/*     */     }
/* 246 */     catch (NoSuchElementException e) {}
/*     */ 
/*     */ 
/*     */     
/* 250 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\CollectionUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */