/*     */ package org.noear.solon.data.cache;
/*     */ 
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.aspect.Invocation;
/*     */ import org.noear.solon.data.annotation.Cache;
/*     */ import org.noear.solon.data.annotation.CachePut;
/*     */ import org.noear.solon.data.annotation.CacheRemove;
/*     */ import org.noear.solon.data.util.InvKeys;
/*     */ import org.noear.solon.ext.SupplierEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheExecutorImp
/*     */ {
/*  18 */   public static final CacheExecutorImp global = new CacheExecutorImp();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object cache(Cache anno, Invocation inv, SupplierEx executor) throws Throwable {
/*  28 */     if (anno == null) {
/*  29 */       return executor.get();
/*     */     }
/*     */ 
/*     */     
/*  33 */     String key = anno.key();
/*  34 */     if (Utils.isEmpty(key)) {
/*     */       
/*  36 */       key = InvKeys.buildByInv(inv);
/*     */     } else {
/*     */       
/*  39 */       key = InvKeys.buildByTmlAndInv(key, inv);
/*     */     } 
/*     */ 
/*     */     
/*  43 */     Object result = null;
/*  44 */     CacheService cs = CacheLib.cacheServiceGet(anno.service());
/*     */     
/*  46 */     String keyLock = key + ":lock";
/*     */     
/*  48 */     synchronized (keyLock.intern()) {
/*     */ 
/*     */ 
/*     */       
/*  52 */       result = cs.get(key);
/*     */       
/*  54 */       if (result == null) {
/*     */ 
/*     */         
/*  57 */         result = executor.get();
/*     */         
/*  59 */         if (result != null) {
/*     */ 
/*     */           
/*  62 */           cs.store(key, result, anno.seconds());
/*     */           
/*  64 */           if (Utils.isNotEmpty(anno.tags())) {
/*  65 */             String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, result);
/*  66 */             CacheTags ct = new CacheTags(cs);
/*     */ 
/*     */             
/*  69 */             for (String tag : tags.split(",")) {
/*  70 */               ct.add(tag, key);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/*  76 */       return result;
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
/*     */   public void cacheRemove(CacheRemove anno, Invocation inv, Object rstValue) {
/*  88 */     if (anno == null) {
/*     */       return;
/*     */     }
/*     */     
/*  92 */     CacheService cs = CacheLib.cacheServiceGet(anno.service());
/*     */ 
/*     */     
/*  95 */     if (Utils.isNotEmpty(anno.keys())) {
/*  96 */       String keys = InvKeys.buildByTmlAndInv(anno.keys(), inv, rstValue);
/*     */       
/*  98 */       for (String key : keys.split(",")) {
/*  99 */         cs.remove(key);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 104 */     if (Utils.isNotEmpty(anno.tags())) {
/* 105 */       String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
/* 106 */       CacheTags ct = new CacheTags(cs);
/*     */       
/* 108 */       for (String tag : tags.split(",")) {
/* 109 */         ct.remove(tag);
/*     */       }
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
/*     */   public void cachePut(CachePut anno, Invocation inv, Object rstValue) {
/* 122 */     if (anno == null) {
/*     */       return;
/*     */     }
/*     */     
/* 126 */     CacheService cs = CacheLib.cacheServiceGet(anno.service());
/*     */ 
/*     */     
/* 129 */     if (Utils.isNotEmpty(anno.key())) {
/* 130 */       String key = InvKeys.buildByTmlAndInv(anno.key(), inv, rstValue);
/* 131 */       cs.store(key, rstValue, anno.seconds());
/*     */     } 
/*     */ 
/*     */     
/* 135 */     if (Utils.isNotEmpty(anno.tags())) {
/* 136 */       String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
/* 137 */       CacheTags ct = new CacheTags(cs);
/*     */       
/* 139 */       for (String tag : tags.split(","))
/* 140 */         ct.update(tag, rstValue, anno.seconds()); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheExecutorImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */