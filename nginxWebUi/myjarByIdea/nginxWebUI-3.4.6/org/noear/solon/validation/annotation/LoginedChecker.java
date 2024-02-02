package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

public interface LoginedChecker {
   boolean check(Logined anno, Context ctx, String userKeyName);
}
