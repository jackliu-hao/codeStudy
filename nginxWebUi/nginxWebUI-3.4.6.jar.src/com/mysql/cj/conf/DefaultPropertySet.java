/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultPropertySet
/*     */   implements PropertySet, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -5156024634430650528L;
/*  47 */   private final Map<PropertyKey, RuntimeProperty<?>> PROPERTY_KEY_TO_RUNTIME_PROPERTY = new HashMap<>();
/*  48 */   private final Map<String, RuntimeProperty<?>> PROPERTY_NAME_TO_RUNTIME_PROPERTY = new HashMap<>();
/*     */   
/*     */   public DefaultPropertySet() {
/*  51 */     for (PropertyDefinition<?> pdef : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.values()) {
/*  52 */       addProperty(pdef.createRuntimeProperty());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addProperty(RuntimeProperty<?> prop) {
/*  58 */     PropertyDefinition<?> def = prop.getPropertyDefinition();
/*  59 */     if (def.getPropertyKey() != null) {
/*  60 */       this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.put(def.getPropertyKey(), prop);
/*     */     } else {
/*  62 */       this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getName(), prop);
/*  63 */       if (def.hasCcAlias()) {
/*  64 */         this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getCcAlias(), prop);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeProperty(String name) {
/*  71 */     PropertyKey key = PropertyKey.fromValue(name);
/*  72 */     if (key != null) {
/*  73 */       this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
/*     */     } else {
/*  75 */       RuntimeProperty<?> prop = this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(name);
/*  76 */       if (prop != null) {
/*  77 */         if (!name.equals(prop.getPropertyDefinition().getName())) {
/*  78 */           this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getName());
/*  79 */         } else if (prop.getPropertyDefinition().hasCcAlias()) {
/*  80 */           this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getCcAlias());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeProperty(PropertyKey key) {
/*  88 */     this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> RuntimeProperty<T> getProperty(String name) {
/*     */     try {
/*  95 */       PropertyKey key = PropertyKey.fromValue(name);
/*  96 */       if (key != null) {
/*  97 */         return getProperty(key);
/*     */       }
/*     */       
/* 100 */       return (RuntimeProperty<T>)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(name);
/*     */     }
/* 102 */     catch (ClassCastException ex) {
/*     */       
/* 104 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> RuntimeProperty<T> getProperty(PropertyKey key) {
/*     */     try {
/* 112 */       RuntimeProperty<T> prop = (RuntimeProperty<T>)this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.get(key);
/*     */       
/* 114 */       if (prop == null) {
/* 115 */         prop = (RuntimeProperty<T>)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(key.getKeyName());
/*     */       }
/* 117 */       return prop;
/*     */     }
/* 119 */     catch (ClassCastException ex) {
/*     */       
/* 121 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Boolean> getBooleanProperty(String name) {
/* 127 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Boolean> getBooleanProperty(PropertyKey key) {
/* 132 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Integer> getIntegerProperty(String name) {
/* 137 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Integer> getIntegerProperty(PropertyKey key) {
/* 142 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Long> getLongProperty(String name) {
/* 147 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Long> getLongProperty(PropertyKey key) {
/* 152 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Integer> getMemorySizeProperty(String name) {
/* 157 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<Integer> getMemorySizeProperty(PropertyKey key) {
/* 162 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<String> getStringProperty(String name) {
/* 167 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RuntimeProperty<String> getStringProperty(PropertyKey key) {
/* 172 */     return getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(String name) {
/* 177 */     return getProperty(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(PropertyKey key) {
/* 182 */     return getProperty(key);
/*     */   }
/*     */   
/*     */   public void initializeProperties(Properties props) {
/* 186 */     if (props != null) {
/* 187 */       Properties infoCopy = (Properties)props.clone();
/*     */ 
/*     */       
/* 190 */       infoCopy.remove(PropertyKey.HOST.getKeyName());
/* 191 */       infoCopy.remove(PropertyKey.PORT.getKeyName());
/* 192 */       infoCopy.remove(PropertyKey.USER.getKeyName());
/* 193 */       infoCopy.remove(PropertyKey.PASSWORD.getKeyName());
/* 194 */       infoCopy.remove(PropertyKey.DBNAME.getKeyName());
/*     */       
/* 196 */       for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
/*     */         try {
/* 198 */           RuntimeProperty<?> propToSet = getProperty(propKey);
/* 199 */           propToSet.initializeFrom(infoCopy, (ExceptionInterceptor)null);
/*     */         }
/* 201 */         catch (CJException e) {
/* 202 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 207 */       RuntimeProperty<PropertyDefinitions.SslMode> sslMode = getEnumProperty(PropertyKey.sslMode);
/* 208 */       if (!sslMode.isExplicitlySet()) {
/* 209 */         RuntimeProperty<Boolean> useSSL = getBooleanProperty(PropertyKey.useSSL);
/* 210 */         RuntimeProperty<Boolean> verifyServerCertificate = getBooleanProperty(PropertyKey.verifyServerCertificate);
/* 211 */         RuntimeProperty<Boolean> requireSSL = getBooleanProperty(PropertyKey.requireSSL);
/* 212 */         if (useSSL.isExplicitlySet() || verifyServerCertificate.isExplicitlySet() || requireSSL.isExplicitlySet()) {
/* 213 */           if (!((Boolean)useSSL.getValue()).booleanValue()) {
/* 214 */             sslMode.setValue(PropertyDefinitions.SslMode.DISABLED);
/* 215 */           } else if (((Boolean)verifyServerCertificate.getValue()).booleanValue()) {
/* 216 */             sslMode.setValue(PropertyDefinitions.SslMode.VERIFY_CA);
/* 217 */           } else if (((Boolean)requireSSL.getValue()).booleanValue()) {
/* 218 */             sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED);
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 224 */       for (Object key : infoCopy.keySet()) {
/* 225 */         String val = infoCopy.getProperty((String)key);
/*     */         
/* 227 */         PropertyDefinition<String> def = new StringPropertyDefinition((String)key, null, val, true, Messages.getString("ConnectionProperties.unknown"), "8.0.10", PropertyDefinitions.CATEGORY_USER_DEFINED, -2147483648);
/* 228 */         RuntimeProperty<String> p = new StringProperty(def);
/* 229 */         addProperty(p);
/*     */       } 
/* 231 */       postInitialization();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void postInitialization() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties exposeAsProperties() {
/* 242 */     Properties props = new Properties();
/* 243 */     for (PropertyKey propKey : this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.keySet()) {
/* 244 */       if (!props.containsKey(propKey.getKeyName())) {
/* 245 */         RuntimeProperty<?> propToGet = getProperty(propKey);
/* 246 */         String propValue = propToGet.getStringValue();
/* 247 */         if (propValue != null) {
/* 248 */           props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 253 */     for (String propName : this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.keySet()) {
/* 254 */       if (!props.containsKey(propName)) {
/* 255 */         RuntimeProperty<?> propToGet = getProperty(propName);
/* 256 */         String propValue = propToGet.getStringValue();
/* 257 */         if (propValue != null) {
/* 258 */           props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
/*     */         }
/*     */       } 
/*     */     } 
/* 262 */     return props;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 267 */     this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
/* 268 */     this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
/* 269 */     postInitialization();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\DefaultPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */