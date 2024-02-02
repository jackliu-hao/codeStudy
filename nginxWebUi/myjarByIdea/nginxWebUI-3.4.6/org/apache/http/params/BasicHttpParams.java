package org.apache.http.params;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicHttpParams extends AbstractHttpParams implements Serializable, Cloneable {
   private static final long serialVersionUID = -7086398485908701455L;
   private final Map<String, Object> parameters = new ConcurrentHashMap();

   public Object getParameter(String name) {
      return this.parameters.get(name);
   }

   public HttpParams setParameter(String name, Object value) {
      if (name == null) {
         return this;
      } else {
         if (value != null) {
            this.parameters.put(name, value);
         } else {
            this.parameters.remove(name);
         }

         return this;
      }
   }

   public boolean removeParameter(String name) {
      if (this.parameters.containsKey(name)) {
         this.parameters.remove(name);
         return true;
      } else {
         return false;
      }
   }

   public void setParameters(String[] names, Object value) {
      String[] arr$ = names;
      int len$ = names.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String name = arr$[i$];
         this.setParameter(name, value);
      }

   }

   public boolean isParameterSet(String name) {
      return this.getParameter(name) != null;
   }

   public boolean isParameterSetLocally(String name) {
      return this.parameters.get(name) != null;
   }

   public void clear() {
      this.parameters.clear();
   }

   public HttpParams copy() {
      try {
         return (HttpParams)this.clone();
      } catch (CloneNotSupportedException var2) {
         throw new UnsupportedOperationException("Cloning not supported");
      }
   }

   public Object clone() throws CloneNotSupportedException {
      BasicHttpParams clone = (BasicHttpParams)super.clone();
      this.copyParams(clone);
      return clone;
   }

   public void copyParams(HttpParams target) {
      Iterator i$ = this.parameters.entrySet().iterator();

      while(i$.hasNext()) {
         Map.Entry<String, Object> me = (Map.Entry)i$.next();
         target.setParameter((String)me.getKey(), me.getValue());
      }

   }

   public Set<String> getNames() {
      return new HashSet(this.parameters.keySet());
   }

   public String toString() {
      return "[parameters=" + this.parameters + "]";
   }
}
