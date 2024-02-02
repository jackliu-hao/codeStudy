package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class NotBlacklistValidator implements Validator<NotBlacklist> {
   public static final NotBlacklistValidator instance = new NotBlacklistValidator();
   private NotBlacklistChecker checker = (anno, ctx) -> {
      return false;
   };

   public void setChecker(NotBlacklistChecker checker) {
      if (checker != null) {
         this.checker = checker;
      }

   }

   public String message(NotBlacklist anno) {
      return anno.message();
   }

   public Class<?>[] groups(NotBlacklist anno) {
      return anno.groups();
   }

   public Result validateOfContext(Context ctx, NotBlacklist anno, String name, StringBuilder tmp) {
      return this.checker.check(anno, ctx) ? Result.succeed() : Result.failure(403);
   }
}
