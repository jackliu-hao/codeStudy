package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

public interface ValidatorFailureHandler {
  @Note("@return 是否停止后续检查器")
  boolean onFailure(Context paramContext, Annotation paramAnnotation, Result paramResult, String paramString) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\ValidatorFailureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */