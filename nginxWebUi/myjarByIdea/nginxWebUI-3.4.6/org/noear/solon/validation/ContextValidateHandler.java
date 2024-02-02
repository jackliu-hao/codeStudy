package org.noear.solon.validation;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

public class ContextValidateHandler implements Handler {
   public void handle(Context ctx) throws Throwable {
      if (!ctx.getHandled()) {
         Action a = ctx.action();
         if (a != null) {
            ValidatorManager.validateOfContext(ctx, a);
         }

      }
   }
}
