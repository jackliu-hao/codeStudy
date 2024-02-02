package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import org.noear.solon.core.handle.Result;

public class BeanValidateInfo extends Result {
   public final Annotation anno;
   public final String message;

   public BeanValidateInfo(Annotation anno, String message) {
      this.anno = anno;
      this.message = message;
   }
}
