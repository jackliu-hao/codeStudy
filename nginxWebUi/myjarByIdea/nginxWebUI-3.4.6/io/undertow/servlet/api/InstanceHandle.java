package io.undertow.servlet.api;

public interface InstanceHandle<T> {
   T getInstance();

   void release();
}
