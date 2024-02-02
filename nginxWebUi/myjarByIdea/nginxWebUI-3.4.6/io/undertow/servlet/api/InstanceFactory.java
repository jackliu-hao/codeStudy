package io.undertow.servlet.api;

public interface InstanceFactory<T> {
   InstanceHandle<T> createInstance() throws InstantiationException;
}
