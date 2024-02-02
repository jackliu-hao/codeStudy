package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Notifier implements Serializable, Cloneable, InputLocationTracker {
   private String type = "mail";
   private boolean sendOnError = true;
   private boolean sendOnFailure = true;
   private boolean sendOnSuccess = true;
   private boolean sendOnWarning = true;
   private String address;
   private Properties configuration;
   private Map<Object, InputLocation> locations;

   public void addConfiguration(String key, String value) {
      this.getConfiguration().put(key, value);
   }

   public Notifier clone() {
      try {
         Notifier copy = (Notifier)super.clone();
         if (this.configuration != null) {
            copy.configuration = (Properties)this.configuration.clone();
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getAddress() {
      return this.address;
   }

   public Properties getConfiguration() {
      if (this.configuration == null) {
         this.configuration = new Properties();
      }

      return this.configuration;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getType() {
      return this.type;
   }

   public boolean isSendOnError() {
      return this.sendOnError;
   }

   public boolean isSendOnFailure() {
      return this.sendOnFailure;
   }

   public boolean isSendOnSuccess() {
      return this.sendOnSuccess;
   }

   public boolean isSendOnWarning() {
      return this.sendOnWarning;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setConfiguration(Properties configuration) {
      this.configuration = configuration;
   }

   public void setLocation(Object key, InputLocation location) {
      if (location != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap();
         }

         this.locations.put(key, location);
      }

   }

   public void setSendOnError(boolean sendOnError) {
      this.sendOnError = sendOnError;
   }

   public void setSendOnFailure(boolean sendOnFailure) {
      this.sendOnFailure = sendOnFailure;
   }

   public void setSendOnSuccess(boolean sendOnSuccess) {
      this.sendOnSuccess = sendOnSuccess;
   }

   public void setSendOnWarning(boolean sendOnWarning) {
      this.sendOnWarning = sendOnWarning;
   }

   public void setType(String type) {
      this.type = type;
   }
}
