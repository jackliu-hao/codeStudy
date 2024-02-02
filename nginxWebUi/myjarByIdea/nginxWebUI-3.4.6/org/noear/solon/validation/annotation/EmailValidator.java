package org.noear.solon.validation.annotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class EmailValidator implements Validator<Email> {
   private static final Map<String, java.util.regex.Pattern> cached = new ConcurrentHashMap();
   public static final EmailValidator instance = new EmailValidator();

   public EmailValidator() {
      cached.putIfAbsent("", java.util.regex.Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"));
   }

   public String message(Email anno) {
      return anno.message();
   }

   public Class<?>[] groups(Email anno) {
      return anno.groups();
   }

   public Result validateOfValue(Email anno, Object val0, StringBuilder tmp) {
      if (val0 != null && !(val0 instanceof String)) {
         return Result.failure();
      } else {
         String val = (String)val0;
         return !this.verify(anno, val) ? Result.failure() : Result.succeed();
      }
   }

   public Result validateOfContext(Context ctx, Email anno, String name, StringBuilder tmp) {
      String val = ctx.param(name);
      return !this.verify(anno, val) ? Result.failure(name) : Result.succeed();
   }

   private boolean verify(Email anno, String val) {
      if (Utils.isEmpty(val)) {
         return true;
      } else {
         java.util.regex.Pattern pt = (java.util.regex.Pattern)cached.get(anno.value());
         if (pt == null) {
            if (!anno.value().contains("@")) {
               throw new IllegalArgumentException("@Email value must have an @ sign");
            }

            pt = java.util.regex.Pattern.compile(anno.value());
            cached.putIfAbsent(anno.value(), pt);
         }

         return pt.matcher(val).find();
      }
   }
}
