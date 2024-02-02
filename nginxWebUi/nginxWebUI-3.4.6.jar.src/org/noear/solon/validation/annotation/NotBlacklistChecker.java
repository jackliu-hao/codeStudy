package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

@FunctionalInterface
public interface NotBlacklistChecker {
  boolean check(NotBlacklist paramNotBlacklist, Context paramContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NotBlacklistChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */