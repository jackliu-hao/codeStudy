package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.util.beans.BeanDescription;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.PropertySetterException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class PropertySetter extends ContextAwareBase {
   protected final Object obj;
   protected final Class<?> objClass;
   protected final BeanDescription beanDescription;

   public PropertySetter(BeanDescriptionCache beanDescriptionCache, Object obj) {
      this.obj = obj;
      this.objClass = obj.getClass();
      this.beanDescription = beanDescriptionCache.getBeanDescription(this.objClass);
   }

   public void setProperty(String name, String value) {
      if (value != null) {
         Method setter = this.findSetterMethod(name);
         if (setter == null) {
            this.addWarn("No setter for property [" + name + "] in " + this.objClass.getName() + ".");
         } else {
            try {
               this.setProperty(setter, name, value);
            } catch (PropertySetterException var5) {
               this.addWarn("Failed to set property [" + name + "] to value \"" + value + "\". ", var5);
            }
         }

      }
   }

   private void setProperty(Method setter, String name, String value) throws PropertySetterException {
      Class<?>[] paramTypes = setter.getParameterTypes();

      Object arg;
      try {
         arg = StringToObjectConverter.convertArg(this, value, paramTypes[0]);
      } catch (Throwable var8) {
         throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. ", var8);
      }

      if (arg == null) {
         throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
      } else {
         try {
            setter.invoke(this.obj, arg);
         } catch (Exception var7) {
            throw new PropertySetterException(var7);
         }
      }
   }

   public AggregationType computeAggregationType(String name) {
      String cName = this.capitalizeFirstLetter(name);
      Method addMethod = this.findAdderMethod(cName);
      if (addMethod != null) {
         AggregationType type = this.computeRawAggregationType(addMethod);
         switch (type) {
            case NOT_FOUND:
               return AggregationType.NOT_FOUND;
            case AS_BASIC_PROPERTY:
               return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
            case AS_COMPLEX_PROPERTY:
               return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
            case AS_BASIC_PROPERTY_COLLECTION:
            case AS_COMPLEX_PROPERTY_COLLECTION:
               this.addError("Unexpected AggregationType " + type);
         }
      }

      Method setter = this.findSetterMethod(name);
      return setter != null ? this.computeRawAggregationType(setter) : AggregationType.NOT_FOUND;
   }

   private Method findAdderMethod(String name) {
      String propertyName = BeanUtil.toLowerCamelCase(name);
      return this.beanDescription.getAdder(propertyName);
   }

   private Method findSetterMethod(String name) {
      String propertyName = BeanUtil.toLowerCamelCase(name);
      return this.beanDescription.getSetter(propertyName);
   }

   private Class<?> getParameterClassForMethod(Method method) {
      if (method == null) {
         return null;
      } else {
         Class<?>[] classArray = method.getParameterTypes();
         return classArray.length != 1 ? null : classArray[0];
      }
   }

   private AggregationType computeRawAggregationType(Method method) {
      Class<?> parameterClass = this.getParameterClassForMethod(method);
      if (parameterClass == null) {
         return AggregationType.NOT_FOUND;
      } else {
         return StringToObjectConverter.canBeBuiltFromSimpleString(parameterClass) ? AggregationType.AS_BASIC_PROPERTY : AggregationType.AS_COMPLEX_PROPERTY;
      }
   }

   private boolean isUnequivocallyInstantiable(Class<?> clazz) {
      if (clazz.isInterface()) {
         return false;
      } else {
         try {
            Object o = clazz.newInstance();
            return o != null;
         } catch (InstantiationException var4) {
            return false;
         } catch (IllegalAccessException var5) {
            return false;
         }
      }
   }

   public Class<?> getObjClass() {
      return this.objClass;
   }

   public void addComplexProperty(String name, Object complexProperty) {
      Method adderMethod = this.findAdderMethod(name);
      if (adderMethod != null) {
         Class<?>[] paramTypes = adderMethod.getParameterTypes();
         if (!this.isSanityCheckSuccessful(name, adderMethod, paramTypes, complexProperty)) {
            return;
         }

         this.invokeMethodWithSingleParameterOnThisObject(adderMethod, complexProperty);
      } else {
         this.addError("Could not find method [add" + name + "] in class [" + this.objClass.getName() + "].");
      }

   }

   void invokeMethodWithSingleParameterOnThisObject(Method method, Object parameter) {
      Class<?> ccc = parameter.getClass();

      try {
         method.invoke(this.obj, parameter);
      } catch (Exception var5) {
         this.addError("Could not invoke method " + method.getName() + " in class " + this.obj.getClass().getName() + " with parameter of type " + ccc.getName(), var5);
      }

   }

   public void addBasicProperty(String name, String strValue) {
      if (strValue != null) {
         name = this.capitalizeFirstLetter(name);
         Method adderMethod = this.findAdderMethod(name);
         if (adderMethod == null) {
            this.addError("No adder for property [" + name + "].");
         } else {
            Class<?>[] paramTypes = adderMethod.getParameterTypes();
            this.isSanityCheckSuccessful(name, adderMethod, paramTypes, strValue);

            Object arg;
            try {
               arg = StringToObjectConverter.convertArg(this, strValue, paramTypes[0]);
            } catch (Throwable var7) {
               this.addError("Conversion to type [" + paramTypes[0] + "] failed. ", var7);
               return;
            }

            if (arg != null) {
               this.invokeMethodWithSingleParameterOnThisObject(adderMethod, strValue);
            }

         }
      }
   }

   public void setComplexProperty(String name, Object complexProperty) {
      Method setter = this.findSetterMethod(name);
      if (setter == null) {
         this.addWarn("Not setter method for property [" + name + "] in " + this.obj.getClass().getName());
      } else {
         Class<?>[] paramTypes = setter.getParameterTypes();
         if (this.isSanityCheckSuccessful(name, setter, paramTypes, complexProperty)) {
            try {
               this.invokeMethodWithSingleParameterOnThisObject(setter, complexProperty);
            } catch (Exception var6) {
               this.addError("Could not set component " + this.obj + " for parent component " + this.obj, var6);
            }

         }
      }
   }

   private boolean isSanityCheckSuccessful(String name, Method method, Class<?>[] params, Object complexProperty) {
      Class<?> ccc = complexProperty.getClass();
      if (params.length != 1) {
         this.addError("Wrong number of parameters in setter method for property [" + name + "] in " + this.obj.getClass().getName());
         return false;
      } else if (!params[0].isAssignableFrom(complexProperty.getClass())) {
         this.addError("A \"" + ccc.getName() + "\" object is not assignable to a \"" + params[0].getName() + "\" variable.");
         this.addError("The class \"" + params[0].getName() + "\" was loaded by ");
         this.addError("[" + params[0].getClassLoader() + "] whereas object of type ");
         this.addError("\"" + ccc.getName() + "\" was loaded by [" + ccc.getClassLoader() + "].");
         return false;
      } else {
         return true;
      }
   }

   private String capitalizeFirstLetter(String name) {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
   }

   public Object getObj() {
      return this.obj;
   }

   Method getRelevantMethod(String name, AggregationType aggregationType) {
      Method relevantMethod;
      if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
         relevantMethod = this.findAdderMethod(name);
      } else {
         if (aggregationType != AggregationType.AS_COMPLEX_PROPERTY) {
            throw new IllegalStateException(aggregationType + " not allowed here");
         }

         relevantMethod = this.findSetterMethod(name);
      }

      return relevantMethod;
   }

   <T extends Annotation> T getAnnotation(String name, Class<T> annonationClass, Method relevantMethod) {
      return relevantMethod != null ? relevantMethod.getAnnotation(annonationClass) : null;
   }

   Class<?> getDefaultClassNameByAnnonation(String name, Method relevantMethod) {
      DefaultClass defaultClassAnnon = (DefaultClass)this.getAnnotation(name, DefaultClass.class, relevantMethod);
      return defaultClassAnnon != null ? defaultClassAnnon.value() : null;
   }

   Class<?> getByConcreteType(String name, Method relevantMethod) {
      Class<?> paramType = this.getParameterClassForMethod(relevantMethod);
      if (paramType == null) {
         return null;
      } else {
         boolean isUnequivocallyInstantiable = this.isUnequivocallyInstantiable(paramType);
         return isUnequivocallyInstantiable ? paramType : null;
      }
   }

   public Class<?> getClassNameViaImplicitRules(String name, AggregationType aggregationType, DefaultNestedComponentRegistry registry) {
      Class<?> registryResult = registry.findDefaultComponentType(this.obj.getClass(), name);
      if (registryResult != null) {
         return registryResult;
      } else {
         Method relevantMethod = this.getRelevantMethod(name, aggregationType);
         if (relevantMethod == null) {
            return null;
         } else {
            Class<?> byAnnotation = this.getDefaultClassNameByAnnonation(name, relevantMethod);
            return byAnnotation != null ? byAnnotation : this.getByConcreteType(name, relevantMethod);
         }
      }
   }
}
