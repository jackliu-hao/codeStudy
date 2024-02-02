package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

@FunctionalInterface
public interface NoRepeatSubmitChecker {
   boolean check(NoRepeatSubmit anno, Context ctx, String submitHash, int limitSeconds);
}
