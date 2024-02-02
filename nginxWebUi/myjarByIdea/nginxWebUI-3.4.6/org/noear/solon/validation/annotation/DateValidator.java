package org.noear.solon.validation.annotation;

import java.time.format.DateTimeFormatter;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class DateValidator implements Validator<Date> {
   public static final DateValidator instance = new DateValidator();

   public String message(Date anno) {
      return anno.message();
   }

   public Class<?>[] groups(Date anno) {
      return anno.groups();
   }

   public Result validateOfValue(Date anno, Object val0, StringBuilder tmp) {
      if (val0 != null && !(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return !this.verify(anno, val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, Date anno, String name, StringBuilder tmp) {
      String val = ctx.param(name);
      return !this.verify(anno, val) ? Result.failure(name) : Result.succeed();
   }

   private boolean verify(Date anno, String val) {
      if (Utils.isEmpty(val)) {
         return true;
      } else {
         try {
            if (Utils.isEmpty(anno.value())) {
               DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
            } else {
               DateTimeFormatter.ofPattern(anno.value()).parse(val);
            }

            return true;
         } catch (Exception var4) {
            return false;
         }
      }
   }
}
