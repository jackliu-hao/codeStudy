package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

@FunctionalInterface
public interface NoRepeatSubmitChecker {
  boolean check(NoRepeatSubmit paramNoRepeatSubmit, Context paramContext, String paramString, int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\NoRepeatSubmitChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */