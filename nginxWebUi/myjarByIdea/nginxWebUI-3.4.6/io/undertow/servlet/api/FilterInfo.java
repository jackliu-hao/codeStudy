package io.undertow.servlet.api;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.util.ConstructorInstanceFactory;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;

public class FilterInfo implements Cloneable {
   private final Class<? extends Filter> filterClass;
   private final String name;
   private volatile InstanceFactory<? extends Filter> instanceFactory;
   private final Map<String, String> initParams = new HashMap();
   private volatile boolean asyncSupported;

   public FilterInfo(String name, Class<? extends Filter> filterClass) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
      } else if (filterClass == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("filterClass", "Filter", name);
      } else if (!Filter.class.isAssignableFrom(filterClass)) {
         throw UndertowServletMessages.MESSAGES.filterMustImplementFilter(name, filterClass);
      } else {
         try {
            Constructor<Filter> ctor = filterClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            this.instanceFactory = new ConstructorInstanceFactory(ctor);
            this.name = name;
            this.filterClass = filterClass;
         } catch (NoSuchMethodException var4) {
            throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Filter", filterClass);
         }
      }
   }

   public FilterInfo(String name, Class<? extends Filter> filterClass, InstanceFactory<? extends Filter> instanceFactory) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("name");
      } else if (filterClass == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("filterClass", "Filter", name);
      } else if (!Filter.class.isAssignableFrom(filterClass)) {
         throw UndertowServletMessages.MESSAGES.filterMustImplementFilter(name, filterClass);
      } else {
         this.instanceFactory = instanceFactory;
         this.name = name;
         this.filterClass = filterClass;
      }
   }

   public void validate() {
   }

   public FilterInfo clone() {
      FilterInfo info = (new FilterInfo(this.name, this.filterClass, this.instanceFactory)).setAsyncSupported(this.asyncSupported);
      info.initParams.putAll(this.initParams);
      return info;
   }

   public Class<? extends Filter> getFilterClass() {
      return this.filterClass;
   }

   public String getName() {
      return this.name;
   }

   public InstanceFactory<? extends Filter> getInstanceFactory() {
      return this.instanceFactory;
   }

   public void setInstanceFactory(InstanceFactory<? extends Filter> instanceFactory) {
      this.instanceFactory = instanceFactory;
   }

   public FilterInfo addInitParam(String name, String value) {
      this.initParams.put(name, value);
      return this;
   }

   public Map<String, String> getInitParams() {
      return Collections.unmodifiableMap(this.initParams);
   }

   public boolean isAsyncSupported() {
      return this.asyncSupported;
   }

   public FilterInfo setAsyncSupported(boolean asyncSupported) {
      this.asyncSupported = asyncSupported;
      return this;
   }

   public String toString() {
      return "FilterInfo{filterClass=" + this.filterClass + ", name='" + this.name + '\'' + '}';
   }
}
