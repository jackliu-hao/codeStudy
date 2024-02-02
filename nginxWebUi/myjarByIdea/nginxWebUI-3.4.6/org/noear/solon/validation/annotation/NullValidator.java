package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class NullValidator implements Validator<Null> {
   public static final NullValidator instance = new NullValidator();

   public String message(Null anno) {
      return anno.message();
   }

   public Class<?>[] groups(Null anno) {
      return anno.groups();
   }

   public Result validateOfValue(Null anno, Object val, StringBuilder tmp) {
      return val != null ? Result.failure() : Result.succeed();
   }

   public Result validateOfContext(Context ctx, Null anno, String name, StringBuilder tmp) {
      if (name == null) {
         String[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String key = var5[var7];
            if (ctx.param(key) != null) {
               tmp.append(',').append(key);
            }
         }
      } else if (ctx.param(name) != null) {
         tmp.append(',').append(name);
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }
}
