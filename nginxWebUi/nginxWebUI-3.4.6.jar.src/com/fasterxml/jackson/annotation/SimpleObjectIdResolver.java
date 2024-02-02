/*    */ package com.fasterxml.jackson.annotation;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public class SimpleObjectIdResolver
/*    */   implements ObjectIdResolver
/*    */ {
/*    */   protected Map<ObjectIdGenerator.IdKey, Object> _items;
/*    */   
/*    */   public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
/* 21 */     if (this._items == null) {
/* 22 */       this._items = new HashMap<ObjectIdGenerator.IdKey, Object>();
/*    */     } else {
/* 24 */       Object old = this._items.get(id);
/* 25 */       if (old != null) {
/*    */         
/* 27 */         if (old == ob) {
/*    */           return;
/*    */         }
/* 30 */         throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
/*    */       } 
/*    */     } 
/*    */     
/* 34 */     this._items.put(id, ob);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object resolveId(ObjectIdGenerator.IdKey id) {
/* 39 */     return (this._items == null) ? null : this._items.get(id);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canUseFor(ObjectIdResolver resolverType) {
/* 44 */     return (resolverType.getClass() == getClass());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ObjectIdResolver newForDeserialization(Object context) {
/* 51 */     return new SimpleObjectIdResolver();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\SimpleObjectIdResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */