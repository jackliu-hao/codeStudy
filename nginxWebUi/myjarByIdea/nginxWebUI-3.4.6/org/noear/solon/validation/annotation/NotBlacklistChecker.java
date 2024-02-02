package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

@FunctionalInterface
public interface NotBlacklistChecker {
   boolean check(NotBlacklist anno, Context ctx);
}
