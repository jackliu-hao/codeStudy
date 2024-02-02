package cn.hutool.extra.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SpringUtil.class})
public @interface EnableSpringUtil {}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\spring\EnableSpringUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */