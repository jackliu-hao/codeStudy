package org.wildfly.common.selector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Deprecated
public @interface DefaultSelector {
  Class<? extends Selector<?>> value();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\DefaultSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */