package org.noear.snack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.noear.snack.core.Feature;

public class ONodeData {
   public OValue value = null;
   public Map<String, ONode> object = null;
   public List<ONode> array = null;
   public ONodeType nodeType;
   protected ONode _n;
   public Map<String, String> attrs;

   public ONodeData(ONode n) {
      this.nodeType = ONodeType.Null;
      this.attrs = null;
      this._n = n;
   }

   public Map<String, ONode> object() {
      this.tryInitObject();
      return this.object;
   }

   public List<ONode> array() {
      this.tryInitArray();
      return this.array;
   }

   public OValue value() {
      this.tryInitValue();
      return this.value;
   }

   protected void tryInitNull() {
      if (this.nodeType != ONodeType.Null) {
         this.nodeType = ONodeType.Null;
         if (this.object != null) {
            this.object.clear();
            this.object = null;
         }

         if (this.array != null) {
            this.array.clear();
            this.array = null;
         }

         this.value = null;
      }

   }

   protected void tryInitValue() {
      if (this.nodeType != ONodeType.Value) {
         this.nodeType = ONodeType.Value;
         if (this.value == null) {
            this.value = new OValue(this._n);
         }
      }

   }

   protected void tryInitObject() {
      if (this.nodeType != ONodeType.Object) {
         this.nodeType = ONodeType.Object;
         if (this.object == null) {
            if (this._n._o.hasFeature(Feature.OrderedField)) {
               this.object = new ONodeLinkedObject();
            } else {
               this.object = new ONodeObject();
            }
         }
      }

   }

   protected void tryInitArray() {
      if (this.nodeType != ONodeType.Array) {
         this.nodeType = ONodeType.Array;
         if (this.array == null) {
            this.array = new ONodeArray();
         }
      }

   }

   protected void shiftToArray() {
      this.tryInitArray();
      if (this.object != null) {
         Iterator var1 = this.object.values().iterator();

         while(var1.hasNext()) {
            ONode n1 = (ONode)var1.next();
            this.array.add(n1);
         }

         this.object.clear();
         this.object = null;
      }

   }

   public String attrGet(String key) {
      return this.attrs != null ? (String)this.attrs.get(key) : null;
   }

   public void attrSet(String key, String val) {
      if (this.attrs == null) {
         this.attrs = new LinkedHashMap();
      }

      this.attrs.put(key, val);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return this.hashCode() == o.hashCode();
      }
   }

   public int hashCode() {
      if (this.nodeType == ONodeType.Object) {
         return this.object.hashCode();
      } else if (this.nodeType == ONodeType.Array) {
         return this.array.hashCode();
      } else {
         return this.nodeType == ONodeType.Value ? this.value.hashCode() : 0;
      }
   }

   class ONodeLinkedObject extends LinkedHashMap<String, ONode> {
      public boolean containsValue(Object value) {
         Iterator var2 = this.values().iterator();

         ONode n;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            n = (ONode)var2.next();
         } while(!n.equals(value));

         return true;
      }
   }

   class ONodeObject extends HashMap<String, ONode> {
      public boolean containsValue(Object value) {
         Iterator var2 = this.values().iterator();

         ONode n;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            n = (ONode)var2.next();
         } while(!n.equals(value));

         return true;
      }
   }

   class ONodeArray extends ArrayList<ONode> {
      public int indexOf(Object o) {
         for(int i = 0; i < this.size(); ++i) {
            if (((ONode)this.get(i)).equals(o)) {
               return i;
            }
         }

         return -1;
      }
   }
}
