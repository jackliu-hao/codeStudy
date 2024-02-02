package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.util.StringUtils;

public class DecimalMinValidator implements Validator<DecimalMin> {
   public static final DecimalMinValidator instance = new DecimalMinValidator();

   public String message(DecimalMin anno) {
      return anno.message();
   }

   public Class<?>[] groups(DecimalMin anno) {
      return anno.groups();
   }

   public Result validateOfValue(DecimalMin anno, Object val0, StringBuilder tmp) {
      if (!(val0 instanceof Double)) {
         return Result.failure();
      } else {
         Double val = (Double)val0;
         return val != null && !(val < anno.value()) ? Result.succeed() : Result.failure();
      }
   }

   public Result validateOfContext(Context ctx, DecimalMin anno, String name, StringBuilder tmp) {
      String val = ctx.param(name);
      return StringUtils.isNumber(val) && !(Double.parseDouble(val) < anno.value()) ? Result.succeed() : Result.failure(name);
   }
}
