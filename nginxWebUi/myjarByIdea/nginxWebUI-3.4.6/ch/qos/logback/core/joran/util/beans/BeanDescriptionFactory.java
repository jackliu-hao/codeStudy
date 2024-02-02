package ch.qos.logback.core.joran.util.beans;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanDescriptionFactory extends ContextAwareBase {
   BeanDescriptionFactory(Context context) {
      this.setContext(context);
   }

   public BeanDescription create(Class<?> clazz) {
      Map<String, Method> propertyNameToGetter = new HashMap();
      Map<String, Method> propertyNameToSetter = new HashMap();
      Map<String, Method> propertyNameToAdder = new HashMap();
      Method[] methods = clazz.getMethods();
      Method[] var6 = methods;
      int var7 = methods.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Method method = var6[var8];
         if (!method.isBridge()) {
            String propertyName;
            Method oldAdder;
            String message;
            if (BeanUtil.isGetter(method)) {
               propertyName = BeanUtil.getPropertyName(method);
               oldAdder = (Method)propertyNameToGetter.put(propertyName, method);
               if (oldAdder != null) {
                  if (oldAdder.getName().startsWith("is")) {
                     propertyNameToGetter.put(propertyName, oldAdder);
                  }

                  message = String.format("Class '%s' contains multiple getters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                  this.addWarn(message);
               }
            } else if (BeanUtil.isSetter(method)) {
               propertyName = BeanUtil.getPropertyName(method);
               oldAdder = (Method)propertyNameToSetter.put(propertyName, method);
               if (oldAdder != null) {
                  message = String.format("Class '%s' contains multiple setters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                  this.addWarn(message);
               }
            } else if (BeanUtil.isAdder(method)) {
               propertyName = BeanUtil.getPropertyName(method);
               oldAdder = (Method)propertyNameToAdder.put(propertyName, method);
               if (oldAdder != null) {
                  message = String.format("Class '%s' contains multiple adders for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                  this.addWarn(message);
               }
            }
         }
      }

      return new BeanDescription(clazz, propertyNameToGetter, propertyNameToSetter, propertyNameToAdder);
   }
}
