/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.PropertyNotModifiableException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractRuntimeProperty<T>
/*     */   implements RuntimeProperty<T>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -3424722534876438236L;
/*     */   private PropertyDefinition<T> propertyDefinition;
/*     */   protected T value;
/*     */   protected T initialValue;
/*     */   protected boolean wasExplicitlySet = false;
/*     */   private List<WeakReference<RuntimeProperty.RuntimePropertyListener>> listeners;
/*     */   
/*     */   public AbstractRuntimeProperty() {}
/*     */   
/*     */   protected AbstractRuntimeProperty(PropertyDefinition<T> propertyDefinition) {
/*  64 */     this.propertyDefinition = propertyDefinition;
/*  65 */     this.value = propertyDefinition.getDefaultValue();
/*  66 */     this.initialValue = propertyDefinition.getDefaultValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyDefinition<T> getPropertyDefinition() {
/*  71 */     return this.propertyDefinition;
/*     */   }
/*     */ 
/*     */   
/*     */   public void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) {
/*  76 */     String name = getPropertyDefinition().getName();
/*  77 */     String alias = getPropertyDefinition().getCcAlias();
/*  78 */     if (extractFrom.containsKey(name)) {
/*  79 */       String extractedValue = (String)extractFrom.remove(name);
/*  80 */       if (extractedValue != null) {
/*  81 */         setValueInternal(extractedValue, exceptionInterceptor);
/*  82 */         this.initialValue = this.value;
/*     */       } 
/*  84 */     } else if (alias != null && extractFrom.containsKey(alias)) {
/*  85 */       String extractedValue = (String)extractFrom.remove(alias);
/*  86 */       if (extractedValue != null) {
/*  87 */         setValueInternal(extractedValue, exceptionInterceptor);
/*  88 */         this.initialValue = this.value;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) {
/*  95 */     RefAddr refAddr = ref.get(getPropertyDefinition().getName());
/*  96 */     if (refAddr != null) {
/*  97 */       String refContentAsString = (String)refAddr.getContent();
/*  98 */       if (refContentAsString != null) {
/*  99 */         setValueInternal(refContentAsString, exceptionInterceptor);
/* 100 */         this.initialValue = this.value;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetValue() {
/* 107 */     this.value = this.initialValue;
/* 108 */     invokeListeners();
/*     */   }
/*     */   
/*     */   public boolean isExplicitlySet() {
/* 112 */     return this.wasExplicitlySet;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(RuntimeProperty.RuntimePropertyListener l) {
/* 117 */     if (this.listeners == null) {
/* 118 */       this.listeners = new ArrayList<>();
/*     */     }
/*     */     
/* 121 */     boolean found = false;
/* 122 */     for (WeakReference<RuntimeProperty.RuntimePropertyListener> weakReference : this.listeners) {
/* 123 */       if (l.equals(weakReference.get())) {
/* 124 */         found = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 128 */     if (!found) {
/* 129 */       this.listeners.add(new WeakReference<>(l));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeListener(RuntimeProperty.RuntimePropertyListener listener) {
/* 135 */     if (this.listeners != null) {
/* 136 */       for (WeakReference<RuntimeProperty.RuntimePropertyListener> wr : this.listeners) {
/* 137 */         RuntimeProperty.RuntimePropertyListener l = wr.get();
/* 138 */         if (l.equals(listener)) {
/* 139 */           this.listeners.remove(wr);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   protected void invokeListeners() {
/* 147 */     if (this.listeners != null) {
/* 148 */       for (WeakReference<RuntimeProperty.RuntimePropertyListener> wr : this.listeners) {
/* 149 */         RuntimeProperty.RuntimePropertyListener l = wr.get();
/* 150 */         if (l != null) {
/* 151 */           l.handlePropertyChange(this); continue;
/*     */         } 
/* 153 */         this.listeners.remove(wr);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T getValue() {
/* 161 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public T getInitialValue() {
/* 166 */     return this.initialValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringValue() {
/* 171 */     return (this.value == null) ? null : this.value.toString();
/*     */   }
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
/*     */   public void setValueInternal(String value, ExceptionInterceptor exceptionInterceptor) {
/* 184 */     setValueInternal(getPropertyDefinition().parseObject(value, exceptionInterceptor), value, exceptionInterceptor);
/*     */   }
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
/*     */   public void setValueInternal(T value, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
/* 198 */     if (getPropertyDefinition().isRangeBased()) {
/* 199 */       checkRange(value, valueAsString, exceptionInterceptor);
/*     */     }
/* 201 */     this.value = value;
/* 202 */     this.wasExplicitlySet = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkRange(T val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(T value) {
/* 221 */     setValue(value, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(T value, ExceptionInterceptor exceptionInterceptor) {
/* 226 */     if (getPropertyDefinition().isRuntimeModifiable()) {
/* 227 */       setValueInternal(value, null, exceptionInterceptor);
/* 228 */       invokeListeners();
/*     */     } else {
/* 230 */       throw (PropertyNotModifiableException)ExceptionFactory.createException(PropertyNotModifiableException.class, 
/* 231 */           Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'" + getPropertyDefinition().getName() + "'" }));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\AbstractRuntimeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */