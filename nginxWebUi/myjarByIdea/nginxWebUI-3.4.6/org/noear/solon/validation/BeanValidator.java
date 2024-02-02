package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

public interface BeanValidator {
   Result validate(Object obj, Class<?>... groups);
}
