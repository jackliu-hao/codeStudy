package io.undertow.servlet.util;

import io.undertow.servlet.api.InstanceHandle;

public class ImmediateInstanceHandle<T> implements InstanceHandle<T> {
   private final T instance;

   public ImmediateInstanceHandle(T instance) {
      this.instance = instance;
   }

   public T getInstance() {
      return this.instance;
   }

   public void release() {
   }
}
