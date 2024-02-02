/*     */ package org.noear.solon.data.cache;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ public class LocalCacheService
/*     */   implements CacheService
/*     */ {
/*  16 */   public static final CacheService instance = new LocalCacheService();
/*     */ 
/*     */   
/*     */   private int _defaultSeconds;
/*     */   
/*  21 */   private Map<String, Entity> _data = new ConcurrentHashMap<>();
/*     */   
/*  23 */   private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();
/*     */   
/*     */   public LocalCacheService() {
/*  26 */     this(30);
/*     */   }
/*     */   
/*     */   public LocalCacheService(int defSeconds) {
/*  30 */     this._defaultSeconds = defSeconds;
/*     */   }
/*     */   
/*     */   public LocalCacheService(Properties prop) {
/*  34 */     String defSeconds_str = prop.getProperty("defSeconds");
/*     */     
/*  36 */     if (Utils.isNotEmpty(defSeconds_str)) {
/*  37 */       this._defaultSeconds = Integer.parseInt(defSeconds_str);
/*     */     }
/*     */     
/*  40 */     if (this._defaultSeconds < 1) {
/*  41 */       this._defaultSeconds = 30;
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
/*     */   
/*     */   public void store(String key, Object obj, int seconds) {
/*  54 */     if (seconds <= 0) {
/*  55 */       seconds = getDefalutSeconds();
/*     */     }
/*     */     
/*  58 */     synchronized (key.intern()) {
/*  59 */       Entity ent = this._data.get(key);
/*  60 */       if (ent == null) {
/*     */         
/*  62 */         ent = new Entity(obj);
/*  63 */         this._data.put(key, ent);
/*     */       } else {
/*     */         
/*  66 */         ent.value = obj;
/*  67 */         ent.futureDel();
/*     */       } 
/*     */       
/*  70 */       if (seconds > 0)
/*     */       {
/*  72 */         ent.future = _exec.schedule(() -> this._data.remove(key), seconds, TimeUnit.SECONDS);
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
/*     */   
/*     */   public Object get(String key) {
/*  86 */     Entity ent = this._data.get(key);
/*     */     
/*  88 */     return (ent == null) ? null : ent.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String key) {
/*  98 */     synchronized (key.intern()) {
/*  99 */       Entity ent = this._data.remove(key);
/*     */       
/* 101 */       if (ent != null) {
/* 102 */         ent.futureDel();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 111 */     for (Entity ent : this._data.values()) {
/* 112 */       ent.futureDel();
/*     */     }
/*     */     
/* 115 */     this._data.clear();
/*     */   }
/*     */   
/*     */   public int getDefalutSeconds() {
/* 119 */     return this._defaultSeconds;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Entity
/*     */   {
/*     */     public Object value;
/*     */     
/*     */     public Future future;
/*     */ 
/*     */     
/*     */     public Entity(Object val) {
/* 131 */       this.value = val;
/*     */     }
/*     */     
/*     */     protected void futureDel() {
/* 135 */       if (this.future != null) {
/* 136 */         this.future.cancel(true);
/* 137 */         this.future = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\LocalCacheService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */