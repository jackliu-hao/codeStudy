package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.util.StringUtils;

public class MaxValidator implements Validator<Max> {
   public static final MaxValidator instance = new MaxValidator();

   public String message(Max anno) {
      return anno.message();
   }

   public Class<?>[] groups(Max anno) {
      return anno.groups();
   }

   public Result validateOfValue(Max anno, Object val0, StringBuilder tmp) {
      if (!(val0 instanceof Number)) {
         return Result.failure();
      } else {
         Number val = (Number)val0;
         return val != null && val.longValue() <= anno.value() ? Result.succeed() : Result.failure();
      }
   }

   public Result validateOfContext(Context ctx, Max anno, String name, StringBuilder tmp) {
      String val = ctx.param(name);
      if (!StringUtils.isInteger(val) || Long.parseLong(val) > anno.value()) {
         tmp.append(',').append(name);
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }
}
