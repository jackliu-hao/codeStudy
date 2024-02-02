package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.util.StringUtils;

public class NotZeroValidator implements Validator<NotZero> {
   public static final NotZeroValidator instance = new NotZeroValidator();

   public String message(NotZero anno) {
      return anno.message();
   }

   public Class<?>[] groups(NotZero anno) {
      return anno.groups();
   }

   public Result validateOfValue(NotZero anno, Object val0, StringBuilder tmp) {
      if (!(val0 instanceof Number)) {
         return Result.failure();
      } else {
         Number val = (Number)val0;
         return val != null && val.longValue() != 0L ? Result.succeed() : Result.failure();
      }
   }

   public Result validateOfContext(Context ctx, NotZero anno, String name, StringBuilder tmp) {
      if (name == null) {
         String[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String key = var5[var7];
            String val = ctx.param(key);
            if (!StringUtils.isInteger(val) || Long.parseLong(val) == 0L) {
               tmp.append(',').append(key);
            }
         }
      } else {
         String val = ctx.param(name);
         if (!StringUtils.isInteger(val) || Long.parseLong(val) == 0L) {
            tmp.append(',').append(name);
         }
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }
}
