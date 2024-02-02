package org.noear.solon.validation;

import org.noear.solon.core.handle.Result;

public class BeanValidatorDefault implements BeanValidator {
   public Result validate(Object obj, Class<?>... groups) {
      return ValidatorManager.validateOfEntity(obj, groups);
   }
}
