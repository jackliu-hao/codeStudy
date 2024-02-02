package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Contributor implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String email;
   private String url;
   private String organization;
   private String organizationUrl;
   private List<String> roles;
   private String timezone;
   private Properties properties;
   private Map<Object, InputLocation> locations;

   public void addProperty(String key, String value) {
      this.getProperties().put(key, value);
   }

   public void addRole(String string) {
      this.getRoles().add(string);
   }

   public Contributor clone() {
      try {
         Contributor copy = (Contributor)super.clone();
         if (this.roles != null) {
            copy.roles = new ArrayList();
            copy.roles.addAll(this.roles);
         }

         if (this.properties != null) {
            copy.properties = (Properties)this.properties.clone();
         }

         if (copy.locations != null) {
            copy.locations = new LinkedHashMap(copy.locations);
         }

         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getEmail() {
      return this.email;
   }

   public InputLocation getLocation(Object key) {
      return this.locations != null ? (InputLocation)this.locations.get(key) : null;
   }

   public String getName() {
      return this.name;
   }

   public String getOrganization() {
      return this.organization;
   }

   public String getOrganizationUrl() {
      return this.organizationUrl;
   }

   public Properties getProperties() {
      if (this.properties == null) {
         this.properties = new Properties();
      }

      return this.properties;
   }

   public List<String> getRoles() {
      if (this.roles == null) {
         this.roles = new ArrayList();
      }

      return this.roles;
   }

   public String getTimezone() {
      return this.timezone;
   }

   public String getUrl() {
      return this.url;
   }

   public void removeRole(String string) {
      this.getRoles().remove(string);
   }

   public void setEmail(String email) {
      this.email = email;
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

   public void setOrganization(String organization) {
      this.organization = organization;
   }

   public void setOrganizationUrl(String organizationUrl) {
      this.organizationUrl = organizationUrl;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public void setRoles(List<String> roles) {
      this.roles = roles;
   }

   public void setTimezone(String timezone) {
      this.timezone = timezone;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
