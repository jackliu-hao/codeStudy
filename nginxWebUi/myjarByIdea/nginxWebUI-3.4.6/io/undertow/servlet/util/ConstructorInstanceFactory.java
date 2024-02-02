package io.undertow.servlet.util;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorInstanceFactory<T> implements InstanceFactory<T> {
   private final Constructor<T> constructor;

   public ConstructorInstanceFactory(Constructor<T> constructor) {
      constructor.setAccessible(true);
      this.constructor = constructor;
   }

   public InstanceHandle<T> createInstance() throws InstantiationException {
      InstantiationException ite;
      try {
         T instance = this.constructor.newInstance();
         return new ImmediateInstanceHandle(instance);
      } catch (IllegalAccessException var3) {
         ite = new InstantiationException();
         ite.initCause(var3);
         throw ite;
      } catch (InvocationTargetException var4) {
         ite = new InstantiationException();
         ite.initCause(var4);
         throw ite;
      }
   }
}
