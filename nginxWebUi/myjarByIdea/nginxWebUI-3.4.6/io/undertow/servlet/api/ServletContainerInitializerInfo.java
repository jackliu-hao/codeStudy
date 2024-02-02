package io.undertow.servlet.api;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.util.ConstructorInstanceFactory;
import java.lang.reflect.Constructor;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;

public class ServletContainerInitializerInfo {
   private final Class<? extends ServletContainerInitializer> servletContainerInitializerClass;
   private final InstanceFactory<? extends ServletContainerInitializer> instanceFactory;
   private final Set<Class<?>> handlesTypes;

   public ServletContainerInitializerInfo(Class<? extends ServletContainerInitializer> servletContainerInitializerClass, InstanceFactory<? extends ServletContainerInitializer> instanceFactory, Set<Class<?>> handlesTypes) {
      this.servletContainerInitializerClass = servletContainerInitializerClass;
      this.instanceFactory = instanceFactory;
      this.handlesTypes = handlesTypes;
   }

   public ServletContainerInitializerInfo(Class<? extends ServletContainerInitializer> servletContainerInitializerClass, Set<Class<?>> handlesTypes) {
      this.servletContainerInitializerClass = servletContainerInitializerClass;
      this.handlesTypes = handlesTypes;

      try {
         Constructor<ServletContainerInitializer> ctor = servletContainerInitializerClass.getDeclaredConstructor();
         ctor.setAccessible(true);
         this.instanceFactory = new ConstructorInstanceFactory(ctor);
      } catch (NoSuchMethodException var4) {
         throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("ServletContainerInitializer", servletContainerInitializerClass);
      }
   }

   public Class<? extends ServletContainerInitializer> getServletContainerInitializerClass() {
      return this.servletContainerInitializerClass;
   }

   public Set<Class<?>> getHandlesTypes() {
      return this.handlesTypes;
   }

   public InstanceFactory<? extends ServletContainerInitializer> getInstanceFactory() {
      return this.instanceFactory;
   }
}
