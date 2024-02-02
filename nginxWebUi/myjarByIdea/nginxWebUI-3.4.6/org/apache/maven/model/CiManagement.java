package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CiManagement implements Serializable, Cloneable, InputLocationTracker {
   private String system;
   private String url;
   private List<Notifier> notifiers;
   private Map<Object, InputLocation> locations;

   public void addNotifier(Notifier notifier) {
      this.getNotifiers().add(notifier);
   }

   public CiManagement clone() {
      try {
         CiManagement copy = (CiManagement)super.clone();
         if (this.notifiers != null) {
            copy.notifiers = new ArrayList();
            Iterator i$ = this.notifiers.iterator();

            while(i$.hasNext()) {
               Notifier item = (Notifier)i$.next();
               copy.notifiers.add(item.clone());
            }
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var4) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var4);
      }
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public List<Notifier> getNotifiers() {
      if (this.notifiers == null) {
         this.notifiers = new ArrayList();
      }

      return this.notifiers;
   }

   public String getSystem() {
      return this.system;
   }

   public String getUrl() {
      return this.url;
   }

   public void removeNotifier(Notifier notifier) {
      this.getNotifiers().remove(notifier);
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setNotifiers(List<Notifier> notifiers) {
      this.notifiers = notifiers;
   }

   public void setSystem(String system) {
      this.system = system;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
