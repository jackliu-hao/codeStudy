package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

class ValidatorFailureHandlerDefault implements ValidatorFailureHandler {
   public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {
      ctx.setHandled(true);
      if (rst.getCode() > 400 && rst.getCode() < 500) {
         ctx.status(rst.getCode());
      } else {
         ctx.status(400);
      }

      if (!ctx.getRendered()) {
         if (Utils.isEmpty(msg)) {
            if (Utils.isEmpty(rst.getDescription())) {
               msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed").toString();
            } else {
               msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed: ").append(rst.getDescription()).toString();
            }
         }

         ctx.render(Result.failure(rst.getCode(), msg));
      }

      return true;
   }
}
