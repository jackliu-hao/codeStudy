package org.noear.solon.validation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

public class BeanValidateInterceptor implements Interceptor {
   public Object doIntercept(Invocation inv) throws Throwable {
      ValidatorManager.validateOfInvocation(inv);
      return inv.invoke();
   }
}
