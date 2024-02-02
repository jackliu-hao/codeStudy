/*    */ package org.noear.solon.sessionstate.local;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.Future;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ class ScheduledStore {
/*    */   private int _defaultSeconds;
/* 13 */   private Map<String, Entity> _data = new ConcurrentHashMap<>();
/* 14 */   private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();
/*    */   
/*    */   public ScheduledStore(int seconds) {
/* 17 */     this._defaultSeconds = seconds;
/*    */   }
/*    */   
/*    */   public Collection<String> keys() {
/* 21 */     return this._data.keySet();
/*    */   }
/*    */   
/*    */   public void put(String block, String key, Object obj) {
/* 25 */     synchronized (block.intern()) {
/* 26 */       Entity ent = this._data.get(block);
/* 27 */       if (ent == null) {
/* 28 */         ent = new Entity();
/* 29 */         this._data.put(block, ent);
/*    */       } else {
/* 31 */         ent.futureDel();
/*    */       } 
/*    */       
/* 34 */       ent.map.put(key, obj);
/*    */       
/* 36 */       ent.future = _exec.schedule(() -> this._data.remove(block), this._defaultSeconds, TimeUnit.SECONDS);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void delay(String block) {
/* 43 */     Entity ent = this._data.get(block);
/* 44 */     if (ent != null) {
/* 45 */       ent.futureDel();
/*    */       
/* 47 */       ent.future = _exec.schedule(() -> this._data.remove(block), this._defaultSeconds, TimeUnit.SECONDS);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Object get(String block, String key) {
/* 54 */     Entity ent = this._data.get(block);
/* 55 */     if (ent != null) {
/* 56 */       return ent.map.get(key);
/*    */     }
/*    */     
/* 59 */     return null;
/*    */   }
/*    */   
/*    */   public void remove(String block, String key) {
/* 63 */     synchronized (block.intern()) {
/* 64 */       Entity ent = this._data.get(block);
/* 65 */       if (ent != null) {
/* 66 */         ent.map.remove(key);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public void clear(String block) {
/* 72 */     synchronized (block.intern()) {
/* 73 */       Entity ent = this._data.get(block);
/* 74 */       if (ent != null) {
/* 75 */         ent.futureDel();
/*    */         
/* 77 */         this._data.remove(block);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void clear() {
/* 83 */     for (Entity ent : this._data.values()) {
/* 84 */       ent.futureDel();
/*    */     }
/*    */     
/* 87 */     this._data.clear();
/*    */   }
/*    */   
/*    */   private static class Entity
/*    */   {
/* 92 */     public Map<String, Object> map = new ConcurrentHashMap<>();
/*    */     public Future future;
/*    */     
/*    */     protected void futureDel() {
/* 96 */       if (this.future != null) {
/* 97 */         this.future.cancel(true);
/* 98 */         this.future = null;
/*    */       } 
/*    */     }
/*    */     
/*    */     private Entity() {}
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\sessionstate\local\ScheduledStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */