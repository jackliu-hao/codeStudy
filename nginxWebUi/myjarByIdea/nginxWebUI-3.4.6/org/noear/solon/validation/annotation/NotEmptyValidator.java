package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class NotEmptyValidator implements Validator<NotEmpty> {
   public static final NotEmptyValidator instance = new NotEmptyValidator();

   public String message(NotEmpty anno) {
      return anno.message();
   }

   public Class<?>[] groups(NotEmpty anno) {
      return anno.groups();
   }

   public Result validateOfValue(NotEmpty anno, Object val0, StringBuilder tmp) {
      if (!(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return Utils.isEmpty(val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, NotEmpty anno, String name, StringBuilder tmp) {
      if (name == null) {
         String[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String key = var5[var7];
            if (Utils.isEmpty(ctx.param(key))) {
               tmp.append(',').append(key);
            }
         }
      } else if (Utils.isEmpty(ctx.param(name))) {
         tmp.append(',').append(name);
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }
}
