package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

public class LoginedCheckerImp implements LoginedChecker {
   public boolean check(Logined anno, Context ctx, String userKeyName) {
      Object userKey = ctx.session(userKeyName);
      if (userKey == null) {
         return false;
      } else if (userKey instanceof Number && ((Number)userKey).longValue() < 1L) {
         return false;
      } else {
         return !(userKey instanceof String) || ((String)userKey).length() >= 1;
      }
   }
}
