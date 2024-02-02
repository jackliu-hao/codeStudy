package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.io.Serializable;

public abstract class AbstractPropertyDefinition<T> implements PropertyDefinition<T>, Serializable {
   private static final long serialVersionUID = 2696624840927848766L;
   private PropertyKey key;
   private String name;
   private String ccAlias;
   private T defaultValue;
   private boolean isRuntimeModifiable;
   private String description;
   private String sinceVersion;
   private String category;
   private int order;
   private int lowerBound;
   private int upperBound;

   public AbstractPropertyDefinition(String name, String camelCaseAlias, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
      this.key = null;
      this.name = name;
      this.ccAlias = camelCaseAlias;
      this.setDefaultValue(defaultValue);
      this.setRuntimeModifiable(isRuntimeModifiable);
      this.setDescription(description);
      this.setSinceVersion(sinceVersion);
      this.setCategory(category);
      this.setOrder(orderInCategory);
   }

   public AbstractPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
      this.key = null;
      this.key = key;
      this.name = key.getKeyName();
      this.ccAlias = key.getCcAlias();
      this.setDefaultValue(defaultValue);
      this.setRuntimeModifiable(isRuntimeModifiable);
      this.setDescription(description);
      this.setSinceVersion(sinceVersion);
      this.setCategory(category);
      this.setOrder(orderInCategory);
   }

   public AbstractPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, int lowerBound, int upperBound) {
      this(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
      this.setLowerBound(lowerBound);
      this.setUpperBound(upperBound);
   }

   public boolean hasValueConstraints() {
      return this.getAllowableValues() != null && this.getAllowableValues().length > 0;
   }

   public boolean isRangeBased() {
      return false;
   }

   public PropertyKey getPropertyKey() {
      return this.key;
   }

   public String getName() {
      return this.name;
   }

   public String getCcAlias() {
      return this.ccAlias;
   }

   public boolean hasCcAlias() {
      return this.ccAlias != null && this.ccAlias.length() > 0;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public void setDefaultValue(T defaultValue) {
      this.defaultValue = defaultValue;
   }

   public boolean isRuntimeModifiable() {
      return this.isRuntimeModifiable;
   }

   public void setRuntimeModifiable(boolean isRuntimeModifiable) {
      this.isRuntimeModifiable = isRuntimeModifiable;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSinceVersion() {
      return this.sinceVersion;
   }

   public void setSinceVersion(String sinceVersion) {
      this.sinceVersion = sinceVersion;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String[] getAllowableValues() {
      return null;
   }

   public int getLowerBound() {
      return this.lowerBound;
   }

   public void setLowerBound(int lowerBound) {
      this.lowerBound = lowerBound;
   }

   public int getUpperBound() {
      return this.upperBound;
   }

   public void setUpperBound(int upperBound) {
      this.upperBound = upperBound;
   }

   public abstract T parseObject(String var1, ExceptionInterceptor var2);
}
