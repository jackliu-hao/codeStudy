package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class WhitelistValidator implements Validator<Whitelist> {
   public static final WhitelistValidator instance = new WhitelistValidator();
   private WhitelistChecker checker = (anno, ctx) -> {
      return false;
   };

   public void setChecker(WhitelistChecker checker) {
      if (checker != null) {
         this.checker = checker;
      }

   }

   public String message(Whitelist anno) {
      return anno.message();
   }

   public Class<?>[] groups(Whitelist anno) {
      return anno.groups();
   }

   public Result validateOfContext(Context ctx, Whitelist anno, String name, StringBuilder tmp) {
      return this.checker.check(anno, ctx) ? Result.succeed() : Result.failure(403);
   }
}
