package org.noear.solon.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Before;
import org.noear.solon.validation.BeanValidateInterceptor;
import org.noear.solon.validation.ContextValidateHandler;

@Inherited
@Before({ContextValidateHandler.class})
@Around(value = BeanValidateInterceptor.class, index = 1)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\annotation\Valid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */