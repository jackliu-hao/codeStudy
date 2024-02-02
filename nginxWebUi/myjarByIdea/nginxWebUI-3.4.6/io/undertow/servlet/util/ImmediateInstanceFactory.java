package io.undertow.servlet.util;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;

public class ImmediateInstanceFactory<T> implements InstanceFactory<T> {
   private final T instance;

   public ImmediateInstanceFactory(T instance) {
      this.instance = instance;
   }

   public InstanceHandle<T> createInstance() throws InstantiationException {
      return new ImmediateInstanceHandle(this.instance);
   }
}
