package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class LengthValidator implements Validator<Length> {
   public static final LengthValidator instance = new LengthValidator();

   public String message(Length anno) {
      return anno.message();
   }

   public Class<?>[] groups(Length anno) {
      return anno.groups();
   }

   public Result validateOfValue(Length anno, Object val0, StringBuilder tmp) {
      if (val0 != null && !(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return !this.verify(anno, val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, Length anno, String name, StringBuilder tmp) {
      String val = ctx.param(name);
      return !this.verify(anno, val) ? Result.failure(name) : Result.succeed();
   }

   private boolean verify(Length anno, String val) {
      if (val == null) {
         return true;
      } else if (anno.min() > 0 && val.length() < anno.min()) {
         return false;
      } else {
         return anno.max() <= 0 || val.length() <= anno.max();
      }
   }
}
