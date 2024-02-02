package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MailingList implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String subscribe;
   private String unsubscribe;
   private String post;
   private String archive;
   private List<String> otherArchives;
   private Map<Object, InputLocation> locations;

   public void addOtherArchive(String string) {
      this.getOtherArchives().add(string);
   }

   public MailingList clone() {
      try {
         MailingList copy = (MailingList)super.clone();
         if (this.otherArchives != null) {
            copy.otherArchives = new ArrayList();
            copy.otherArchives.addAll(this.otherArchives);
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getArchive() {
      return this.archive;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getOtherArchives() {
      if (this.otherArchives == null) {
         this.otherArchives = new ArrayList();
      }

      return this.otherArchives;
   }

   public String getPost() {
      return this.post;
   }

   public String getSubscribe() {
      return this.subscribe;
   }

   public String getUnsubscribe() {
      return this.unsubscribe;
   }

   public void removeOtherArchive(String string) {
      this.getOtherArchives().remove(string);
   }

   public void setArchive(String archive) {
      this.archive = archive;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setName(String name) {
      this.name = name;
   }

   public void setOtherArchives(List<String> otherArchives) {
      this.otherArchives = otherArchives;
   }

   public void setPost(String post) {
      this.post = post;
   }

   public void setSubscribe(String subscribe) {
      this.subscribe = subscribe;
   }

   public void setUnsubscribe(String unsubscribe) {
      this.unsubscribe = unsubscribe;
   }
}
