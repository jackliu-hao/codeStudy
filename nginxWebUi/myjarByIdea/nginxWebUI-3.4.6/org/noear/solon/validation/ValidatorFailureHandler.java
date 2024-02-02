package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

public interface ValidatorFailureHandler {
   @Note("@return 是否停止后续检查器")
   boolean onFailure(Context ctx, Annotation ano, Result result, String message) throws Throwable;
}
