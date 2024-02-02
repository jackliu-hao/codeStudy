package org.noear.solon.validation.annotation;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.BeanValidator;
import org.noear.solon.validation.BeanValidatorDefault;
import org.noear.solon.validation.Validator;

public class ValidatedValidator implements Validator<Validated> {
   public static final ValidatedValidator instance = new ValidatedValidator();
   private BeanValidator validator = new BeanValidatorDefault();

   public ValidatedValidator() {
      Aop.getAsyn(BeanValidator.class, (bw) -> {
         this.validator = (BeanValidator)bw.get();
      });
   }

   public Class<?>[] groups(Validated anno) {
      return anno.value();
   }

   public Result validateOfValue(Validated anno, Object val, StringBuilder tmp) {
      return this.validator.validate(val, anno.value());
   }

   public Result validateOfContext(Context ctx, Validated anno, String name, StringBuilder tmp) {
      return null;
   }
}
