package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class LoginedValidator implements Validator<Logined> {
   public static final LoginedValidator instance = new LoginedValidator();
   private LoginedChecker checker = new LoginedCheckerImp();
   public static String sessionUserKeyName = "user_id";

   public void setChecker(LoginedChecker checker) {
      if (checker != null) {
         this.checker = checker;
      }

   }

   public String message(Logined anno) {
      return anno.message();
   }

   public Class<?>[] groups(Logined anno) {
      return anno.groups();
   }

   public Result validateOfContext(Context ctx, Logined anno, String name, StringBuilder tmp) {
      String userKeyName = anno.value();
      if (Utils.isEmpty(userKeyName)) {
         userKeyName = sessionUserKeyName;
      }

      return this.checker.check(anno, ctx, userKeyName) ? Result.succeed() : Result.failure();
   }
}
