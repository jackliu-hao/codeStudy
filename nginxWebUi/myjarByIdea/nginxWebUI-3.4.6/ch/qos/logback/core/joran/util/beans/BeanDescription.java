package ch.qos.logback.core.joran.util.beans;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class BeanDescription {
   private final Class<?> clazz;
   private final Map<String, Method> propertyNameToGetter;
   private final Map<String, Method> propertyNameToSetter;
   private final Map<String, Method> propertyNameToAdder;

   protected BeanDescription(Class<?> clazz, Map<String, Method> propertyNameToGetter, Map<String, Method> propertyNameToSetter, Map<String, Method> propertyNameToAdder) {
      this.clazz = clazz;
      this.propertyNameToGetter = Collections.unmodifiableMap(propertyNameToGetter);
      this.propertyNameToSetter = Collections.unmodifiableMap(propertyNameToSetter);
      this.propertyNameToAdder = Collections.unmodifiableMap(propertyNameToAdder);
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public Map<String, Method> getPropertyNameToGetter() {
      return this.propertyNameToGetter;
   }

   public Map<String, Method> getPropertyNameToSetter() {
      return this.propertyNameToSetter;
   }

   public Method getGetter(String propertyName) {
      return (Method)this.propertyNameToGetter.get(propertyName);
   }

   public Method getSetter(String propertyName) {
      return (Method)this.propertyNameToSetter.get(propertyName);
   }

   public Map<String, Method> getPropertyNameToAdder() {
      return this.propertyNameToAdder;
   }

   public Method getAdder(String propertyName) {
      return (Method)this.propertyNameToAdder.get(propertyName);
   }
}
