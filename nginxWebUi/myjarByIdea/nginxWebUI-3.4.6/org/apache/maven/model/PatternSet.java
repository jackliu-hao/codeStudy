package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatternSet implements Serializable, Cloneable, InputLocationTracker {
   private List<String> includes;
   private List<String> excludes;
   private Map<Object, InputLocation> locations;

   public void addExclude(String string) {
      this.getExcludes().add(string);
   }

   public void addInclude(String string) {
      this.getIncludes().add(string);
   }

   public PatternSet clone() {
      try {
         PatternSet copy = (PatternSet)super.clone();
         if (this.includes != null) {
            copy.includes = new ArrayList();
            copy.includes.addAll(this.includes);
         }

         if (this.excludes != null) {
            copy.excludes = new ArrayList();
            copy.excludes.addAll(this.excludes);
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public List<String> getExcludes() {
      if (this.excludes == null) {
         this.excludes = new ArrayList();
      }

      return this.excludes;
   }

   public List<String> getIncludes() {
      if (this.includes == null) {
         this.includes = new ArrayList();
      }

      return this.includes;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public void removeExclude(String string) {
      this.getExcludes().remove(string);
   }

   public void removeInclude(String string) {
      this.getIncludes().remove(string);
   }

   public void setExcludes(List<String> excludes) {
      this.excludes = excludes;
   }

   public void setIncludes(List<String> includes) {
      this.includes = includes;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder(128);
      sb.append("PatternSet [includes: {");
      Iterator i = this.getIncludes().iterator();

      String str;
      while(i.hasNext()) {
         str = (String)i.next();
         sb.append(str).append(", ");
      }

      if (sb.substring(sb.length() - 2).equals(", ")) {
         sb.delete(sb.length() - 2, sb.length());
      }

      sb.append("}, excludes: {");
      i = this.getExcludes().iterator();

      while(i.hasNext()) {
         str = (String)i.next();
         sb.append(str).append(", ");
      }

      if (sb.substring(sb.length() - 2).equals(", ")) {
         sb.delete(sb.length() - 2, sb.length());
      }

      sb.append("}]");
      return sb.toString();
   }
}
