package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

@FunctionalInterface
public interface Validator<T extends Annotation> {
   default String message(T anno) {
      return "";
   }

   default Class<?>[] groups(T anno) {
      return null;
   }

   default Result validateOfValue(T anno, Object val, StringBuilder tmp) {
      return Result.failure();
   }

   Result validateOfContext(Context ctx, T anno, String name, StringBuilder tmp);
}
