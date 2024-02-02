package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;

public class LongProperty extends AbstractRuntimeProperty<Long> {
   private static final long serialVersionUID = 1814429804634837665L;

   protected LongProperty(PropertyDefinition<Long> propertyDefinition) {
      super(propertyDefinition);
   }

   protected void checkRange(Long val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
      if (val < (long)this.getPropertyDefinition().getLowerBound() || val > (long)this.getPropertyDefinition().getUpperBound()) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + this.getPropertyDefinition().getName() + "' only accepts long integer values in the range of " + this.getPropertyDefinition().getLowerBound() + " - " + this.getPropertyDefinition().getUpperBound() + ", the value '" + (valueAsString == null ? val : valueAsString) + "' exceeds this range.", exceptionInterceptor);
      }
   }
}
