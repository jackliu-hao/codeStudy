package org.apache.maven.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InputLocation implements Serializable, Cloneable, InputLocationTracker {
   private int lineNumber = -1;
   private int columnNumber = -1;
   private InputSource source;
   private Map<Object, InputLocation> locations;

   public InputLocation(int lineNumber, int columnNumber) {
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
   }

   public InputLocation(int lineNumber, int columnNumber, InputSource source) {
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
      this.source = source;
   }

   public InputLocation clone() {
      try {
         InputLocation copy = (InputLocation)super.clone();
         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public Map<Object, InputLocation> getLocations() {
      return this.locations;
   }

   public InputSource getSource() {
      return this.source;
   }

   public static InputLocation merge(InputLocation target, InputLocation source, boolean sourceDominant) {
      if (source == null) {
         return target;
      } else if (target == null) {
         return source;
      } else {
         InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
         Map<Object, InputLocation> sourceLocations = source.getLocations();
         Map<Object, InputLocation> targetLocations = target.getLocations();
         Object locations;
         if (sourceLocations == null) {
            locations = targetLocations;
         } else if (targetLocations == null) {
            locations = sourceLocations;
         } else {
            locations = new LinkedHashMap();
            ((Map)locations).putAll(sourceDominant ? targetLocations : sourceLocations);
            ((Map)locations).putAll(sourceDominant ? sourceLocations : targetLocations);
         }

         result.setLocations((Map)locations);
         return result;
      }
   }

   public static InputLocation merge(InputLocation target, InputLocation source, Collection<Integer> indices) {
      if (source == null) {
         return target;
      } else if (target == null) {
         return source;
      } else {
         InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
         Map<Object, InputLocation> sourceLocations = source.getLocations();
         Map<Object, InputLocation> targetLocations = target.getLocations();
         Object locations;
         if (sourceLocations == null) {
            locations = targetLocations;
         } else if (targetLocations == null) {
            locations = sourceLocations;
         } else {
            locations = new LinkedHashMap();

            InputLocation location;
            for(Iterator<Integer> it = indices.iterator(); it.hasNext(); ((Map)locations).put(((Map)locations).size(), location)) {
               Integer index = (Integer)it.next();
               if (index < 0) {
                  location = (InputLocation)sourceLocations.get(~index);
               } else {
                  location = (InputLocation)targetLocations.get(index);
               }
            }
         }

         result.setLocations((Map)locations);
         return result;
      }
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setLocations(Map<Object, InputLocation> locations) {
      this.locations = locations;
   }
}
