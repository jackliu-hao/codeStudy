package org.noear.solon.validation.annotation;

import java.util.Collection;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class SizeValidator implements Validator<Size> {
   public static final SizeValidator instance = new SizeValidator();

   public String message(Size anno) {
      return anno.message();
   }

   public Class<?>[] groups(Size anno) {
      return anno.groups();
   }

   public Result validateOfValue(Size anno, Object val0, StringBuilder tmp) {
      if (val0 != null && !(val0 instanceof Collection)) {
         return Result.failure();
      } else {
         Collection val = (Collection)val0;
         return !this.verify(anno, val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, Size anno, String name, StringBuilder tmp) {
      return Result.failure();
   }

   private boolean verify(Size anno, Collection val) {
      if (val == null) {
         return true;
      } else if (anno.min() > 0 && val.size() < anno.min()) {
         return false;
      } else {
         return anno.max() <= 0 || val.size() <= anno.max();
      }
   }
}
