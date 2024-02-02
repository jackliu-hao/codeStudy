package com.beust.jcommander;

import com.beust.jcommander.validators.NoValidator;
import com.beust.jcommander.validators.NoValueValidator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ParameterDescription {
   private Object m_object;
   private WrappedParameter m_wrappedParameter;
   private Parameter m_parameterAnnotation;
   private DynamicParameter m_dynamicParameterAnnotation;
   private Parameterized m_parameterized;
   private boolean m_assigned = false;
   private java.util.ResourceBundle m_bundle;
   private String m_description;
   private JCommander m_jCommander;
   private Object m_default;
   private String m_longestName = "";

   public ParameterDescription(Object object, DynamicParameter annotation, Parameterized parameterized, java.util.ResourceBundle bundle, JCommander jc) {
      if (!Map.class.isAssignableFrom(parameterized.getType())) {
         throw new ParameterException("@DynamicParameter " + parameterized.getName() + " should be of type " + "Map but is " + parameterized.getType().getName());
      } else {
         this.m_dynamicParameterAnnotation = annotation;
         this.m_wrappedParameter = new WrappedParameter(this.m_dynamicParameterAnnotation);
         this.init(object, parameterized, bundle, jc);
      }
   }

   public ParameterDescription(Object object, Parameter annotation, Parameterized parameterized, java.util.ResourceBundle bundle, JCommander jc) {
      this.m_parameterAnnotation = annotation;
      this.m_wrappedParameter = new WrappedParameter(this.m_parameterAnnotation);
      this.init(object, parameterized, bundle, jc);
   }

   private java.util.ResourceBundle findResourceBundle(Object o) {
      java.util.ResourceBundle result = null;
      Parameters p = (Parameters)o.getClass().getAnnotation(Parameters.class);
      if (p != null && !this.isEmpty(p.resourceBundle())) {
         result = java.util.ResourceBundle.getBundle(p.resourceBundle(), Locale.getDefault());
      } else {
         ResourceBundle a = (ResourceBundle)o.getClass().getAnnotation(ResourceBundle.class);
         if (a != null && !this.isEmpty(a.value())) {
            result = java.util.ResourceBundle.getBundle(a.value(), Locale.getDefault());
         }
      }

      return result;
   }

   private boolean isEmpty(String s) {
      return s == null || "".equals(s);
   }

   private void initDescription(String description, String descriptionKey, String[] names) {
      this.m_description = description;
      if (!"".equals(descriptionKey) && this.m_bundle != null) {
         this.m_description = this.m_bundle.getString(descriptionKey);
      }

      String[] arr$ = names;
      int len$ = names.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String name = arr$[i$];
         if (name.length() > this.m_longestName.length()) {
            this.m_longestName = name;
         }
      }

   }

   private void init(Object object, Parameterized parameterized, java.util.ResourceBundle bundle, JCommander jCommander) {
      this.m_object = object;
      this.m_parameterized = parameterized;
      this.m_bundle = bundle;
      if (this.m_bundle == null) {
         this.m_bundle = this.findResourceBundle(object);
      }

      this.m_jCommander = jCommander;
      if (this.m_parameterAnnotation != null) {
         String description;
         if (Enum.class.isAssignableFrom(parameterized.getType()) && this.m_parameterAnnotation.description().isEmpty()) {
            description = "Options: " + EnumSet.allOf(parameterized.getType());
         } else {
            description = this.m_parameterAnnotation.description();
         }

         this.initDescription(description, this.m_parameterAnnotation.descriptionKey(), this.m_parameterAnnotation.names());
      } else {
         if (this.m_dynamicParameterAnnotation == null) {
            throw new AssertionError("Shound never happen");
         }

         this.initDescription(this.m_dynamicParameterAnnotation.description(), this.m_dynamicParameterAnnotation.descriptionKey(), this.m_dynamicParameterAnnotation.names());
      }

      try {
         this.m_default = parameterized.get(object);
      } catch (Exception var6) {
      }

      if (this.m_default != null && this.m_parameterAnnotation != null) {
         this.validateDefaultValues(this.m_parameterAnnotation.names());
      }

   }

   private void validateDefaultValues(String[] names) {
      String name = names.length > 0 ? names[0] : "";
      this.validateValueParameter(name, this.m_default);
   }

   public String getLongestName() {
      return this.m_longestName;
   }

   public Object getDefault() {
      return this.m_default;
   }

   public String getDescription() {
      return this.m_description;
   }

   public Object getObject() {
      return this.m_object;
   }

   public String getNames() {
      StringBuilder sb = new StringBuilder();
      String[] names = this.m_wrappedParameter.names();

      for(int i = 0; i < names.length; ++i) {
         if (i > 0) {
            sb.append(", ");
         }

         sb.append(names[i]);
      }

      return sb.toString();
   }

   public WrappedParameter getParameter() {
      return this.m_wrappedParameter;
   }

   public Parameterized getParameterized() {
      return this.m_parameterized;
   }

   private boolean isMultiOption() {
      Class<?> fieldType = this.m_parameterized.getType();
      return fieldType.equals(List.class) || fieldType.equals(Set.class) || this.m_parameterized.isDynamicParameter();
   }

   public void addValue(String value) {
      this.addValue(value, false);
   }

   public boolean isAssigned() {
      return this.m_assigned;
   }

   public void setAssigned(boolean b) {
      this.m_assigned = b;
   }

   public void addValue(String value, boolean isDefault) {
      p("Adding " + (isDefault ? "default " : "") + "value:" + value + " to parameter:" + this.m_parameterized.getName());
      String name = this.m_wrappedParameter.names()[0];
      if ((!this.m_assigned || this.isMultiOption() || this.m_jCommander.isParameterOverwritingAllowed()) && !this.isNonOverwritableForced()) {
         this.validateParameter(name, value);
         Class<?> type = this.m_parameterized.getType();
         Object convertedValue = this.m_jCommander.convertValue(this, value);
         this.validateValueParameter(name, convertedValue);
         boolean isCollection = Collection.class.isAssignableFrom(type);
         if (isCollection) {
            Collection<Object> l = (Collection)this.m_parameterized.get(this.m_object);
            if (l == null || this.fieldIsSetForTheFirstTime(isDefault)) {
               l = this.newCollection(type);
               this.m_parameterized.set(this.m_object, l);
            }

            if (convertedValue instanceof Collection) {
               l.addAll((Collection)convertedValue);
            } else {
               l.add(convertedValue);
            }
         } else {
            this.m_wrappedParameter.addValue(this.m_parameterized, this.m_object, convertedValue);
         }

         if (!isDefault) {
            this.m_assigned = true;
         }

      } else {
         throw new ParameterException("Can only specify option " + name + " once.");
      }
   }

   private void validateParameter(String name, String value) {
      Class<? extends IParameterValidator> validator = this.m_wrappedParameter.validateWith();
      if (validator != null) {
         validateParameter(this, validator, name, value);
      }

   }

   private void validateValueParameter(String name, Object value) {
      Class<? extends IValueValidator> validator = this.m_wrappedParameter.validateValueWith();
      if (validator != null) {
         validateValueParameter(validator, name, value);
      }

   }

   public static void validateValueParameter(Class<? extends IValueValidator> validator, String name, Object value) {
      try {
         if (validator != NoValueValidator.class) {
            p("Validating value parameter:" + name + " value:" + value + " validator:" + validator);
         }

         ((IValueValidator)validator.newInstance()).validate(name, value);
      } catch (InstantiationException var4) {
         throw new ParameterException("Can't instantiate validator:" + var4);
      } catch (IllegalAccessException var5) {
         throw new ParameterException("Can't instantiate validator:" + var5);
      }
   }

   public static void validateParameter(ParameterDescription pd, Class<? extends IParameterValidator> validator, String name, String value) {
      try {
         if (validator != NoValidator.class) {
            p("Validating parameter:" + name + " value:" + value + " validator:" + validator);
         }

         ((IParameterValidator)validator.newInstance()).validate(name, value);
         if (IParameterValidator2.class.isAssignableFrom(validator)) {
            IParameterValidator2 instance = (IParameterValidator2)validator.newInstance();
            instance.validate(name, value, pd);
         }

      } catch (InstantiationException var5) {
         throw new ParameterException("Can't instantiate validator:" + var5);
      } catch (IllegalAccessException var6) {
         throw new ParameterException("Can't instantiate validator:" + var6);
      } catch (ParameterException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new ParameterException(var8);
      }
   }

   private Collection<Object> newCollection(Class<?> type) {
      if (SortedSet.class.isAssignableFrom(type)) {
         return new TreeSet();
      } else if (LinkedHashSet.class.isAssignableFrom(type)) {
         return new LinkedHashSet();
      } else if (Set.class.isAssignableFrom(type)) {
         return new HashSet();
      } else if (List.class.isAssignableFrom(type)) {
         return new ArrayList();
      } else {
         throw new ParameterException("Parameters of Collection type '" + type.getSimpleName() + "' are not supported. Please use List or Set instead.");
      }
   }

   private boolean fieldIsSetForTheFirstTime(boolean isDefault) {
      return !isDefault && !this.m_assigned;
   }

   private static void p(String string) {
      if (System.getProperty("jcommander.debug") != null) {
         JCommander.getConsole().println("[ParameterDescription] " + string);
      }

   }

   public String toString() {
      return "[ParameterDescription " + this.m_parameterized.getName() + "]";
   }

   public boolean isDynamicParameter() {
      return this.m_dynamicParameterAnnotation != null;
   }

   public boolean isHelp() {
      return this.m_wrappedParameter.isHelp();
   }

   public boolean isNonOverwritableForced() {
      return this.m_wrappedParameter.isNonOverwritableForced();
   }
}
