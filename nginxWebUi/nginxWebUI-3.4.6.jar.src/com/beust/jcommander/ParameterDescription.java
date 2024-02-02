/*     */ package com.beust.jcommander;
/*     */ 
/*     */ import com.beust.jcommander.validators.NoValidator;
/*     */ import com.beust.jcommander.validators.NoValueValidator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
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
/*     */ public class ParameterDescription
/*     */ {
/*     */   private Object m_object;
/*     */   private WrappedParameter m_wrappedParameter;
/*     */   private Parameter m_parameterAnnotation;
/*     */   private DynamicParameter m_dynamicParameterAnnotation;
/*     */   private Parameterized m_parameterized;
/*     */   private boolean m_assigned = false;
/*     */   private ResourceBundle m_bundle;
/*     */   private String m_description;
/*     */   private JCommander m_jCommander;
/*     */   private Object m_default;
/*  53 */   private String m_longestName = "";
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterDescription(Object object, DynamicParameter annotation, Parameterized parameterized, ResourceBundle bundle, JCommander jc) {
/*  58 */     if (!Map.class.isAssignableFrom(parameterized.getType())) {
/*  59 */       throw new ParameterException("@DynamicParameter " + parameterized.getName() + " should be of type " + "Map but is " + parameterized.getType().getName());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  64 */     this.m_dynamicParameterAnnotation = annotation;
/*  65 */     this.m_wrappedParameter = new WrappedParameter(this.m_dynamicParameterAnnotation);
/*  66 */     init(object, parameterized, bundle, jc);
/*     */   }
/*     */ 
/*     */   
/*     */   public ParameterDescription(Object object, Parameter annotation, Parameterized parameterized, ResourceBundle bundle, JCommander jc) {
/*  71 */     this.m_parameterAnnotation = annotation;
/*  72 */     this.m_wrappedParameter = new WrappedParameter(this.m_parameterAnnotation);
/*  73 */     init(object, parameterized, bundle, jc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResourceBundle findResourceBundle(Object o) {
/*  82 */     ResourceBundle result = null;
/*     */     
/*  84 */     Parameters p = o.getClass().<Parameters>getAnnotation(Parameters.class);
/*  85 */     if (p != null && !isEmpty(p.resourceBundle())) {
/*  86 */       result = ResourceBundle.getBundle(p.resourceBundle(), Locale.getDefault());
/*     */     } else {
/*  88 */       ResourceBundle a = o.getClass().<ResourceBundle>getAnnotation(ResourceBundle.class);
/*     */       
/*  90 */       if (a != null && !isEmpty(a.value())) {
/*  91 */         result = ResourceBundle.getBundle(a.value(), Locale.getDefault());
/*     */       }
/*     */     } 
/*     */     
/*  95 */     return result;
/*     */   }
/*     */   
/*     */   private boolean isEmpty(String s) {
/*  99 */     return (s == null || "".equals(s));
/*     */   }
/*     */   
/*     */   private void initDescription(String description, String descriptionKey, String[] names) {
/* 103 */     this.m_description = description;
/* 104 */     if (!"".equals(descriptionKey) && 
/* 105 */       this.m_bundle != null) {
/* 106 */       this.m_description = this.m_bundle.getString(descriptionKey);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     for (String name : names) {
/* 115 */       if (name.length() > this.m_longestName.length()) this.m_longestName = name;
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void init(Object object, Parameterized parameterized, ResourceBundle bundle, JCommander jCommander) {
/* 122 */     this.m_object = object;
/* 123 */     this.m_parameterized = parameterized;
/* 124 */     this.m_bundle = bundle;
/* 125 */     if (this.m_bundle == null) {
/* 126 */       this.m_bundle = findResourceBundle(object);
/*     */     }
/* 128 */     this.m_jCommander = jCommander;
/*     */     
/* 130 */     if (this.m_parameterAnnotation != null) {
/*     */       String description;
/* 132 */       if (Enum.class.isAssignableFrom(parameterized.getType()) && this.m_parameterAnnotation.description().isEmpty()) {
/*     */         
/* 134 */         description = "Options: " + EnumSet.allOf(parameterized.getType());
/*     */       } else {
/* 136 */         description = this.m_parameterAnnotation.description();
/*     */       } 
/* 138 */       initDescription(description, this.m_parameterAnnotation.descriptionKey(), this.m_parameterAnnotation.names());
/*     */     }
/* 140 */     else if (this.m_dynamicParameterAnnotation != null) {
/* 141 */       initDescription(this.m_dynamicParameterAnnotation.description(), this.m_dynamicParameterAnnotation.descriptionKey(), this.m_dynamicParameterAnnotation.names());
/*     */     }
/*     */     else {
/*     */       
/* 145 */       throw new AssertionError("Shound never happen");
/*     */     } 
/*     */     
/*     */     try {
/* 149 */       this.m_default = parameterized.get(object);
/* 150 */     } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 156 */     if (this.m_default != null && 
/* 157 */       this.m_parameterAnnotation != null) {
/* 158 */       validateDefaultValues(this.m_parameterAnnotation.names());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void validateDefaultValues(String[] names) {
/* 164 */     String name = (names.length > 0) ? names[0] : "";
/* 165 */     validateValueParameter(name, this.m_default);
/*     */   }
/*     */   
/*     */   public String getLongestName() {
/* 169 */     return this.m_longestName;
/*     */   }
/*     */   
/*     */   public Object getDefault() {
/* 173 */     return this.m_default;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/* 177 */     return this.m_description;
/*     */   }
/*     */   
/*     */   public Object getObject() {
/* 181 */     return this.m_object;
/*     */   }
/*     */   
/*     */   public String getNames() {
/* 185 */     StringBuilder sb = new StringBuilder();
/* 186 */     String[] names = this.m_wrappedParameter.names();
/* 187 */     for (int i = 0; i < names.length; i++) {
/* 188 */       if (i > 0) sb.append(", "); 
/* 189 */       sb.append(names[i]);
/*     */     } 
/* 191 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public WrappedParameter getParameter() {
/* 195 */     return this.m_wrappedParameter;
/*     */   }
/*     */   
/*     */   public Parameterized getParameterized() {
/* 199 */     return this.m_parameterized;
/*     */   }
/*     */   
/*     */   private boolean isMultiOption() {
/* 203 */     Class<?> fieldType = this.m_parameterized.getType();
/* 204 */     return (fieldType.equals(List.class) || fieldType.equals(Set.class) || this.m_parameterized.isDynamicParameter());
/*     */   }
/*     */ 
/*     */   
/*     */   public void addValue(String value) {
/* 209 */     addValue(value, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAssigned() {
/* 216 */     return this.m_assigned;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAssigned(boolean b) {
/* 221 */     this.m_assigned = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(String value, boolean isDefault) {
/* 230 */     p("Adding " + (isDefault ? "default " : "") + "value:" + value + " to parameter:" + this.m_parameterized.getName());
/*     */     
/* 232 */     String name = this.m_wrappedParameter.names()[0];
/* 233 */     if ((this.m_assigned && !isMultiOption() && !this.m_jCommander.isParameterOverwritingAllowed()) || isNonOverwritableForced()) {
/* 234 */       throw new ParameterException("Can only specify option " + name + " once.");
/*     */     }
/*     */     
/* 237 */     validateParameter(name, value);
/*     */     
/* 239 */     Class<?> type = this.m_parameterized.getType();
/*     */     
/* 241 */     Object convertedValue = this.m_jCommander.convertValue(this, value);
/* 242 */     validateValueParameter(name, convertedValue);
/* 243 */     boolean isCollection = Collection.class.isAssignableFrom(type);
/*     */     
/* 245 */     if (isCollection) {
/*     */       
/* 247 */       Collection<Object> l = (Collection<Object>)this.m_parameterized.get(this.m_object);
/* 248 */       if (l == null || fieldIsSetForTheFirstTime(isDefault)) {
/* 249 */         l = newCollection(type);
/* 250 */         this.m_parameterized.set(this.m_object, l);
/*     */       } 
/* 252 */       if (convertedValue instanceof Collection) {
/* 253 */         l.addAll((Collection)convertedValue);
/*     */       } else {
/* 255 */         l.add(convertedValue);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 260 */       this.m_wrappedParameter.addValue(this.m_parameterized, this.m_object, convertedValue);
/*     */     } 
/* 262 */     if (!isDefault) this.m_assigned = true; 
/*     */   }
/*     */   
/*     */   private void validateParameter(String name, String value) {
/* 266 */     Class<? extends IParameterValidator> validator = this.m_wrappedParameter.validateWith();
/* 267 */     if (validator != null) {
/* 268 */       validateParameter(this, validator, name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   private void validateValueParameter(String name, Object value) {
/* 273 */     Class<? extends IValueValidator> validator = this.m_wrappedParameter.validateValueWith();
/* 274 */     if (validator != null) {
/* 275 */       validateValueParameter(validator, name, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void validateValueParameter(Class<? extends IValueValidator> validator, String name, Object value) {
/*     */     try {
/* 282 */       if (validator != NoValueValidator.class) {
/* 283 */         p("Validating value parameter:" + name + " value:" + value + " validator:" + validator);
/*     */       }
/* 285 */       ((IValueValidator<Object>)validator.newInstance()).validate(name, value);
/* 286 */     } catch (InstantiationException e) {
/* 287 */       throw new ParameterException("Can't instantiate validator:" + e);
/* 288 */     } catch (IllegalAccessException e) {
/* 289 */       throw new ParameterException("Can't instantiate validator:" + e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void validateParameter(ParameterDescription pd, Class<? extends IParameterValidator> validator, String name, String value) {
/*     */     try {
/* 297 */       if (validator != NoValidator.class) {
/* 298 */         p("Validating parameter:" + name + " value:" + value + " validator:" + validator);
/*     */       }
/* 300 */       ((IParameterValidator)validator.newInstance()).validate(name, value);
/* 301 */       if (IParameterValidator2.class.isAssignableFrom(validator)) {
/* 302 */         IParameterValidator2 instance = (IParameterValidator2)validator.newInstance();
/* 303 */         instance.validate(name, value, pd);
/*     */       } 
/* 305 */     } catch (InstantiationException e) {
/* 306 */       throw new ParameterException("Can't instantiate validator:" + e);
/* 307 */     } catch (IllegalAccessException e) {
/* 308 */       throw new ParameterException("Can't instantiate validator:" + e);
/* 309 */     } catch (ParameterException ex) {
/* 310 */       throw ex;
/* 311 */     } catch (Exception ex) {
/* 312 */       throw new ParameterException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Collection<Object> newCollection(Class<?> type) {
/* 324 */     if (SortedSet.class.isAssignableFrom(type)) return new TreeSet(); 
/* 325 */     if (LinkedHashSet.class.isAssignableFrom(type)) return new LinkedHashSet(); 
/* 326 */     if (Set.class.isAssignableFrom(type)) return new HashSet(); 
/* 327 */     if (List.class.isAssignableFrom(type)) return new ArrayList();
/*     */     
/* 329 */     throw new ParameterException("Parameters of Collection type '" + type.getSimpleName() + "' are not supported. Please use List or Set instead.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean fieldIsSetForTheFirstTime(boolean isDefault) {
/* 339 */     return (!isDefault && !this.m_assigned);
/*     */   }
/*     */   
/*     */   private static void p(String string) {
/* 343 */     if (System.getProperty("jcommander.debug") != null) {
/* 344 */       JCommander.getConsole().println("[ParameterDescription] " + string);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 350 */     return "[ParameterDescription " + this.m_parameterized.getName() + "]";
/*     */   }
/*     */   
/*     */   public boolean isDynamicParameter() {
/* 354 */     return (this.m_dynamicParameterAnnotation != null);
/*     */   }
/*     */   
/*     */   public boolean isHelp() {
/* 358 */     return this.m_wrappedParameter.isHelp();
/*     */   }
/*     */   
/*     */   public boolean isNonOverwritableForced() {
/* 362 */     return this.m_wrappedParameter.isNonOverwritableForced();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\ParameterDescription.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */