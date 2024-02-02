package io.undertow.servlet.api;

public interface ClassIntrospecter {
   <T> InstanceFactory<T> createInstanceFactory(Class<T> var1) throws NoSuchMethodException;
}
