/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
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
/*     */ public class SegmentConstantPoolArrayCache
/*     */ {
/*  37 */   protected IdentityHashMap knownArrays = new IdentityHashMap<>(1000);
/*     */ 
/*     */   
/*     */   protected List lastIndexes;
/*     */ 
/*     */   
/*     */   protected String[] lastArray;
/*     */ 
/*     */   
/*     */   protected String lastKey;
/*     */ 
/*     */ 
/*     */   
/*     */   public List indexesForArrayKey(String[] array, String key) {
/*  51 */     if (!arrayIsCached(array)) {
/*  52 */       cacheArray(array);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  61 */     if (this.lastArray == array && this.lastKey == key) {
/*  62 */       return this.lastIndexes;
/*     */     }
/*     */ 
/*     */     
/*  66 */     this.lastArray = array;
/*  67 */     this.lastKey = key;
/*  68 */     this.lastIndexes = ((CachedArray)this.knownArrays.get(array)).indexesForKey(key);
/*     */     
/*  70 */     return this.lastIndexes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean arrayIsCached(String[] array) {
/*  81 */     if (!this.knownArrays.containsKey(array)) {
/*  82 */       return false;
/*     */     }
/*  84 */     CachedArray cachedArray = (CachedArray)this.knownArrays.get(array);
/*  85 */     if (cachedArray.lastKnownSize() != array.length) {
/*  86 */       return false;
/*     */     }
/*  88 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void cacheArray(String[] array) {
/*  97 */     if (arrayIsCached(array)) {
/*  98 */       throw new IllegalArgumentException("Trying to cache an array that already exists");
/*     */     }
/* 100 */     this.knownArrays.put(array, new CachedArray(array));
/*     */     
/* 102 */     this.lastArray = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected class CachedArray
/*     */   {
/*     */     String[] primaryArray;
/*     */     
/*     */     int lastKnownSize;
/*     */     
/*     */     HashMap primaryTable;
/*     */ 
/*     */     
/*     */     public CachedArray(String[] array) {
/* 116 */       this.primaryArray = array;
/* 117 */       this.lastKnownSize = array.length;
/* 118 */       this.primaryTable = new HashMap<>(this.lastKnownSize);
/* 119 */       cacheIndexes();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int lastKnownSize() {
/* 129 */       return this.lastKnownSize;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public List indexesForKey(String key) {
/* 141 */       if (!this.primaryTable.containsKey(key)) {
/* 142 */         return Collections.EMPTY_LIST;
/*     */       }
/* 144 */       return (List)this.primaryTable.get(key);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void cacheIndexes() {
/* 153 */       for (int index = 0; index < this.primaryArray.length; index++) {
/* 154 */         String key = this.primaryArray[index];
/* 155 */         if (!this.primaryTable.containsKey(key)) {
/* 156 */           this.primaryTable.put(key, new ArrayList());
/*     */         }
/* 158 */         ((ArrayList<Integer>)this.primaryTable.get(key)).add(Integer.valueOf(index));
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\SegmentConstantPoolArrayCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */