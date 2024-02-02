package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class NotBlankValidator implements Validator<NotBlank> {
   public static final NotBlankValidator instance = new NotBlankValidator();

   public String message(NotBlank anno) {
      return anno.message();
   }

   public Class<?>[] groups(NotBlank anno) {
      return anno.groups();
   }

   public Result validateOfValue(NotBlank anno, Object val0, StringBuilder tmp) {
      if (!(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return Utils.isBlank(val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, NotBlank anno, String name, StringBuilder tmp) {
      if (name == null) {
         String[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String key = var5[var7];
            if (Utils.isBlank(ctx.param(key))) {
               tmp.append(',').append(key);
            }
         }
      } else if (Utils.isBlank(ctx.param(name))) {
         tmp.append(',').append(name);
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }
}
