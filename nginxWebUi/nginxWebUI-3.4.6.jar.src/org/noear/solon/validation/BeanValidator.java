package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

public interface BeanValidator {
  Result validate(Object paramObject, Class<?>... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\BeanValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */