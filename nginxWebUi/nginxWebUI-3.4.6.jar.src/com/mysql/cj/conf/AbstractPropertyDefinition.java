/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class AbstractPropertyDefinition<T>
/*     */   implements PropertyDefinition<T>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2696624840927848766L;
/*  40 */   private PropertyKey key = null;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private String ccAlias;
/*     */   
/*     */   private T defaultValue;
/*     */   private boolean isRuntimeModifiable;
/*     */   private String description;
/*     */   private String sinceVersion;
/*     */   private String category;
/*     */   private int order;
/*     */   private int lowerBound;
/*     */   private int upperBound;
/*     */   
/*     */   public AbstractPropertyDefinition(String name, String camelCaseAlias, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/*  56 */     this.name = name;
/*  57 */     this.ccAlias = camelCaseAlias;
/*  58 */     setDefaultValue(defaultValue);
/*  59 */     setRuntimeModifiable(isRuntimeModifiable);
/*  60 */     setDescription(description);
/*  61 */     setSinceVersion(sinceVersion);
/*  62 */     setCategory(category);
/*  63 */     setOrder(orderInCategory);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/*  69 */     this.key = key;
/*  70 */     this.name = key.getKeyName();
/*  71 */     this.ccAlias = key.getCcAlias();
/*  72 */     setDefaultValue(defaultValue);
/*  73 */     setRuntimeModifiable(isRuntimeModifiable);
/*  74 */     setDescription(description);
/*  75 */     setSinceVersion(sinceVersion);
/*  76 */     setCategory(category);
/*  77 */     setOrder(orderInCategory);
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, int lowerBound, int upperBound) {
/*  82 */     this(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*  83 */     setLowerBound(lowerBound);
/*  84 */     setUpperBound(upperBound);
/*     */   }
/*     */   
/*     */   public boolean hasValueConstraints() {
/*  88 */     return (getAllowableValues() != null && (getAllowableValues()).length > 0);
/*     */   }
/*     */   
/*     */   public boolean isRangeBased() {
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyKey getPropertyKey() {
/*  97 */     return this.key;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 101 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCcAlias() {
/* 106 */     return this.ccAlias;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasCcAlias() {
/* 111 */     return (this.ccAlias != null && this.ccAlias.length() > 0);
/*     */   }
/*     */   
/*     */   public T getDefaultValue() {
/* 115 */     return this.defaultValue;
/*     */   }
/*     */   
/*     */   public void setDefaultValue(T defaultValue) {
/* 119 */     this.defaultValue = defaultValue;
/*     */   }
/*     */   
/*     */   public boolean isRuntimeModifiable() {
/* 123 */     return this.isRuntimeModifiable;
/*     */   }
/*     */   
/*     */   public void setRuntimeModifiable(boolean isRuntimeModifiable) {
/* 127 */     this.isRuntimeModifiable = isRuntimeModifiable;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/* 131 */     return this.description;
/*     */   }
/*     */   
/*     */   public void setDescription(String description) {
/* 135 */     this.description = description;
/*     */   }
/*     */   
/*     */   public String getSinceVersion() {
/* 139 */     return this.sinceVersion;
/*     */   }
/*     */   
/*     */   public void setSinceVersion(String sinceVersion) {
/* 143 */     this.sinceVersion = sinceVersion;
/*     */   }
/*     */   
/*     */   public String getCategory() {
/* 147 */     return this.category;
/*     */   }
/*     */   
/*     */   public void setCategory(String category) {
/* 151 */     this.category = category;
/*     */   }
/*     */   
/*     */   public int getOrder() {
/* 155 */     return this.order;
/*     */   }
/*     */   
/*     */   public void setOrder(int order) {
/* 159 */     this.order = order;
/*     */   }
/*     */   
/*     */   public String[] getAllowableValues() {
/* 163 */     return null;
/*     */   }
/*     */   
/*     */   public int getLowerBound() {
/* 167 */     return this.lowerBound;
/*     */   }
/*     */   
/*     */   public void setLowerBound(int lowerBound) {
/* 171 */     this.lowerBound = lowerBound;
/*     */   }
/*     */   
/*     */   public int getUpperBound() {
/* 175 */     return this.upperBound;
/*     */   }
/*     */   
/*     */   public void setUpperBound(int upperBound) {
/* 179 */     this.upperBound = upperBound;
/*     */   }
/*     */   
/*     */   public abstract T parseObject(String paramString, ExceptionInterceptor paramExceptionInterceptor);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\AbstractPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */