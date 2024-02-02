package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionInterceptor;
import java.util.Properties;
import javax.naming.Reference;

public interface RuntimeProperty<T> {
   PropertyDefinition<T> getPropertyDefinition();

   void initializeFrom(Properties var1, ExceptionInterceptor var2);

   void initializeFrom(Reference var1, ExceptionInterceptor var2);

   void resetValue();

   boolean isExplicitlySet();

   void addListener(RuntimePropertyListener var1);

   void removeListener(RuntimePropertyListener var1);

   T getValue();

   T getInitialValue();

   String getStringValue();

   void setValue(T var1);

   void setValue(T var1, ExceptionInterceptor var2);

   @FunctionalInterface
   public interface RuntimePropertyListener {
      void handlePropertyChange(RuntimeProperty<?> var1);
   }
}
