package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

public interface WhitelistChecker {
   boolean check(Whitelist anno, Context ctx);
}
