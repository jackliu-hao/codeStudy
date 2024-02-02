package org.apache.commons.compress.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ServiceLoaderIterator<E> implements Iterator<E> {
   private E nextServiceLoader;
   private final Class<E> service;
   private final Iterator<E> serviceLoaderIterator;

   public ServiceLoaderIterator(Class<E> service) {
      this(service, ClassLoader.getSystemClassLoader());
   }

   public ServiceLoaderIterator(Class<E> service, ClassLoader classLoader) {
      this.service = service;
      this.serviceLoaderIterator = ServiceLoader.load(service, classLoader).iterator();
   }

   public boolean hasNext() {
      while(this.nextServiceLoader == null) {
         try {
            if (!this.serviceLoaderIterator.hasNext()) {
               return false;
            }

            this.nextServiceLoader = this.serviceLoaderIterator.next();
         } catch (ServiceConfigurationError var2) {
            if (!(var2.getCause() instanceof SecurityException)) {
               throw var2;
            }
         }
      }

      return true;
   }

   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("No more elements for service " + this.service.getName());
      } else {
         E tempNext = this.nextServiceLoader;
         this.nextServiceLoader = null;
         return tempNext;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("service=" + this.service.getName());
   }
}
