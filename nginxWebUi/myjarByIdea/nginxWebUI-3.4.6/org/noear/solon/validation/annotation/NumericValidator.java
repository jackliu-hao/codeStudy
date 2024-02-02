package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.util.StringUtils;

public class NumericValidator implements Validator<Numeric> {
   public static final NumericValidator instance = new NumericValidator();

   public String message(Numeric anno) {
      return anno.message();
   }

   public Class<?>[] groups(Numeric anno) {
      return anno.groups();
   }

   public Result validateOfValue(Numeric anno, Object val0, StringBuilder tmp) {
      if (val0 != null && !(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return !this.verify(anno, val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, Numeric anno, String name, StringBuilder tmp) {
      if (name == null) {
         String[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String key = var5[var7];
            String val = ctx.param(key);
            if (!this.verify(anno, val)) {
               tmp.append(',').append(key);
            }
         }
      } else {
         String val = ctx.param(name);
         if (!this.verify(anno, val)) {
            tmp.append(',').append(name);
         }
      }

      return tmp.length() > 1 ? Result.failure(tmp.substring(1)) : Result.succeed();
   }

   private boolean verify(Numeric anno, String val) {
      return Utils.isEmpty(val) ? true : StringUtils.isNumber(val);
   }
}
