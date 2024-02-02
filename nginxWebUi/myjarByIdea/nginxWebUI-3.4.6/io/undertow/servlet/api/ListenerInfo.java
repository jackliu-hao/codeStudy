package io.undertow.servlet.api;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.util.ConstructorInstanceFactory;
import java.lang.reflect.Constructor;
import java.util.EventListener;

public class ListenerInfo {
   private final Class<? extends EventListener> listenerClass;
   private volatile InstanceFactory<? extends EventListener> instanceFactory;
   private final boolean programatic;

   public ListenerInfo(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory) {
      this(listenerClass, instanceFactory, false);
   }

   public ListenerInfo(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory, boolean programatic) {
      this.listenerClass = listenerClass;
      this.instanceFactory = instanceFactory;
      this.programatic = programatic;
      if (!ApplicationListeners.isListenerClass(listenerClass)) {
         throw UndertowServletMessages.MESSAGES.listenerMustImplementListenerClass(listenerClass);
      }
   }

   public ListenerInfo(Class<? extends EventListener> listenerClass) {
      this(listenerClass, false);
   }

   public ListenerInfo(Class<? extends EventListener> listenerClass, boolean programatic) {
      this.listenerClass = listenerClass;
      this.programatic = programatic;

      try {
         Constructor<EventListener> ctor = listenerClass.getDeclaredConstructor();
         ctor.setAccessible(true);
         this.instanceFactory = new ConstructorInstanceFactory(ctor);
      } catch (NoSuchMethodException var4) {
         throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Listener", listenerClass);
      }
   }

   public InstanceFactory<? extends EventListener> getInstanceFactory() {
      return this.instanceFactory;
   }

   public void setInstanceFactory(InstanceFactory<? extends EventListener> instanceFactory) {
      this.instanceFactory = instanceFactory;
   }

   public boolean isProgramatic() {
      return this.programatic;
   }

   public Class<? extends EventListener> getListenerClass() {
      return this.listenerClass;
   }

   public String toString() {
      return "ListenerInfo{listenerClass=" + this.listenerClass + '}';
   }
}
