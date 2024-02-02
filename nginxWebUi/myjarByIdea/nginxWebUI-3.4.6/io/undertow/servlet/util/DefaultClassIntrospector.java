package io.undertow.servlet.util;

import io.undertow.servlet.api.ClassIntrospecter;
import io.undertow.servlet.api.InstanceFactory;

public class DefaultClassIntrospector implements ClassIntrospecter {
   public static final DefaultClassIntrospector INSTANCE = new DefaultClassIntrospector();

   private DefaultClassIntrospector() {
   }

   public <T> InstanceFactory<T> createInstanceFactory(Class<T> clazz) throws NoSuchMethodException {
      return new ConstructorInstanceFactory(clazz.getDeclaredConstructor());
   }
}
