package com.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

public class SimpleObjectIdResolver implements ObjectIdResolver {
   protected Map<ObjectIdGenerator.IdKey, Object> _items;

   public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
      if (this._items == null) {
         this._items = new HashMap();
      } else {
         Object old = this._items.get(id);
         if (old != null) {
            if (old == ob) {
               return;
            }

            throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
         }
      }

      this._items.put(id, ob);
   }

   public Object resolveId(ObjectIdGenerator.IdKey id) {
      return this._items == null ? null : this._items.get(id);
   }

   public boolean canUseFor(ObjectIdResolver resolverType) {
      return resolverType.getClass() == this.getClass();
   }

   public ObjectIdResolver newForDeserialization(Object context) {
      return new SimpleObjectIdResolver();
   }
}
