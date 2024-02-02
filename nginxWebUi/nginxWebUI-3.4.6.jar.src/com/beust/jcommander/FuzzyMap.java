/*    */ package com.beust.jcommander;
/*    */ 
/*    */ import com.beust.jcommander.internal.Maps;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FuzzyMap
/*    */ {
/*    */   public static <V> V findInMap(Map<? extends IKey, V> map, IKey name, boolean caseSensitive, boolean allowAbbreviations) {
/* 18 */     if (allowAbbreviations) {
/* 19 */       return findAbbreviatedValue(map, name, caseSensitive);
/*    */     }
/* 21 */     if (caseSensitive) {
/* 22 */       return map.get(name);
/*    */     }
/* 24 */     for (IKey c : map.keySet()) {
/* 25 */       if (c.getName().equalsIgnoreCase(name.getName())) {
/* 26 */         return map.get(c);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 31 */     return null;
/*    */   }
/*    */   
/*    */   private static <V> V findAbbreviatedValue(Map<? extends IKey, V> map, IKey name, boolean caseSensitive) {
/*    */     V result;
/* 36 */     String string = name.getName();
/* 37 */     Map<String, V> results = Maps.newHashMap();
/* 38 */     for (IKey c : map.keySet()) {
/* 39 */       String n = c.getName();
/* 40 */       boolean match = ((caseSensitive && n.startsWith(string)) || (!caseSensitive && n.toLowerCase().startsWith(string.toLowerCase())));
/*    */       
/* 42 */       if (match) {
/* 43 */         results.put(n, map.get(c));
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 48 */     if (results.size() > 1) {
/* 49 */       throw new ParameterException("Ambiguous option: " + name + " matches " + results.keySet());
/*    */     }
/* 51 */     if (results.size() == 1) {
/* 52 */       result = results.values().iterator().next();
/*    */     } else {
/* 54 */       result = null;
/*    */     } 
/*    */     
/* 57 */     return result;
/*    */   }
/*    */   
/*    */   static interface IKey {
/*    */     String getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\FuzzyMap.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */