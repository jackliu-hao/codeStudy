package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DefaultPropertySet implements PropertySet, Serializable {
   private static final long serialVersionUID = -5156024634430650528L;
   private final Map<PropertyKey, RuntimeProperty<?>> PROPERTY_KEY_TO_RUNTIME_PROPERTY = new HashMap();
   private final Map<String, RuntimeProperty<?>> PROPERTY_NAME_TO_RUNTIME_PROPERTY = new HashMap();

   public DefaultPropertySet() {
      Iterator var1 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.values().iterator();

      while(var1.hasNext()) {
         PropertyDefinition<?> pdef = (PropertyDefinition)var1.next();
         this.addProperty(pdef.createRuntimeProperty());
      }

   }

   public void addProperty(RuntimeProperty<?> prop) {
      PropertyDefinition<?> def = prop.getPropertyDefinition();
      if (def.getPropertyKey() != null) {
         this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.put(def.getPropertyKey(), prop);
      } else {
         this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getName(), prop);
         if (def.hasCcAlias()) {
            this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getCcAlias(), prop);
         }
      }

   }

   public void removeProperty(String name) {
      PropertyKey key = PropertyKey.fromValue(name);
      if (key != null) {
         this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
      } else {
         RuntimeProperty<?> prop = (RuntimeProperty)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(name);
         if (prop != null) {
            if (!name.equals(prop.getPropertyDefinition().getName())) {
               this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getName());
            } else if (prop.getPropertyDefinition().hasCcAlias()) {
               this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getCcAlias());
            }
         }
      }

   }

   public void removeProperty(PropertyKey key) {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
   }

   public <T> RuntimeProperty<T> getProperty(String name) {
      try {
         PropertyKey key = PropertyKey.fromValue(name);
         return key != null ? this.getProperty(key) : (RuntimeProperty)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(name);
      } catch (ClassCastException var3) {
         throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
      }
   }

   public <T> RuntimeProperty<T> getProperty(PropertyKey key) {
      try {
         RuntimeProperty<T> prop = (RuntimeProperty)this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.get(key);
         if (prop == null) {
            prop = (RuntimeProperty)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(key.getKeyName());
         }

         return prop;
      } catch (ClassCastException var3) {
         throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
      }
   }

   public RuntimeProperty<Boolean> getBooleanProperty(String name) {
      return this.getProperty(name);
   }

   public RuntimeProperty<Boolean> getBooleanProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public RuntimeProperty<Integer> getIntegerProperty(String name) {
      return this.getProperty(name);
   }

   public RuntimeProperty<Integer> getIntegerProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public RuntimeProperty<Long> getLongProperty(String name) {
      return this.getProperty(name);
   }

   public RuntimeProperty<Long> getLongProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public RuntimeProperty<Integer> getMemorySizeProperty(String name) {
      return this.getProperty(name);
   }

   public RuntimeProperty<Integer> getMemorySizeProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public RuntimeProperty<String> getStringProperty(String name) {
      return this.getProperty(name);
   }

   public RuntimeProperty<String> getStringProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(String name) {
      return this.getProperty(name);
   }

   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   public void initializeProperties(Properties props) {
      if (props != null) {
         Properties infoCopy = (Properties)props.clone();
         infoCopy.remove(PropertyKey.HOST.getKeyName());
         infoCopy.remove(PropertyKey.PORT.getKeyName());
         infoCopy.remove(PropertyKey.USER.getKeyName());
         infoCopy.remove(PropertyKey.PASSWORD.getKeyName());
         infoCopy.remove(PropertyKey.DBNAME.getKeyName());
         Iterator var3 = PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet().iterator();

         RuntimeProperty verifyServerCertificate;
         while(var3.hasNext()) {
            PropertyKey propKey = (PropertyKey)var3.next();

            try {
               verifyServerCertificate = this.getProperty(propKey);
               verifyServerCertificate.initializeFrom((Properties)infoCopy, (ExceptionInterceptor)null);
            } catch (CJException var9) {
               throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var9.getMessage(), (Throwable)var9);
            }
         }

         RuntimeProperty<PropertyDefinitions.SslMode> sslMode = this.getEnumProperty(PropertyKey.sslMode);
         if (!sslMode.isExplicitlySet()) {
            RuntimeProperty<Boolean> useSSL = this.getBooleanProperty(PropertyKey.useSSL);
            verifyServerCertificate = this.getBooleanProperty(PropertyKey.verifyServerCertificate);
            RuntimeProperty<Boolean> requireSSL = this.getBooleanProperty(PropertyKey.requireSSL);
            if (useSSL.isExplicitlySet() || verifyServerCertificate.isExplicitlySet() || requireSSL.isExplicitlySet()) {
               if (!(Boolean)useSSL.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.DISABLED);
               } else if ((Boolean)verifyServerCertificate.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.VERIFY_CA);
               } else if ((Boolean)requireSSL.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED);
               }
            }
         }

         Iterator var12 = infoCopy.keySet().iterator();

         while(var12.hasNext()) {
            Object key = var12.next();
            String val = infoCopy.getProperty((String)key);
            PropertyDefinition<String> def = new StringPropertyDefinition((String)key, (String)null, val, true, Messages.getString("ConnectionProperties.unknown"), "8.0.10", PropertyDefinitions.CATEGORY_USER_DEFINED, Integer.MIN_VALUE);
            RuntimeProperty<String> p = new StringProperty(def);
            this.addProperty(p);
         }

         this.postInitialization();
      }

   }

   public void postInitialization() {
   }

   public Properties exposeAsProperties() {
      Properties props = new Properties();
      Iterator var2 = this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.keySet().iterator();

      RuntimeProperty propToGet;
      String propValue;
      while(var2.hasNext()) {
         PropertyKey propKey = (PropertyKey)var2.next();
         if (!props.containsKey(propKey.getKeyName())) {
            propToGet = this.getProperty(propKey);
            propValue = propToGet.getStringValue();
            if (propValue != null) {
               props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
            }
         }
      }

      var2 = this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.keySet().iterator();

      while(var2.hasNext()) {
         String propName = (String)var2.next();
         if (!props.containsKey(propName)) {
            propToGet = this.getProperty(propName);
            propValue = propToGet.getStringValue();
            if (propValue != null) {
               props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
            }
         }
      }

      return props;
   }

   public void reset() {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.values().forEach((p) -> {
         p.resetValue();
      });
      this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.values().forEach((p) -> {
         p.resetValue();
      });
      this.postInitialization();
   }
}
