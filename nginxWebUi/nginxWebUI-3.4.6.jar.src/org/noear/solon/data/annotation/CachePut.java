package org.noear.solon.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {
  @Note("缓存服务")
  String service() default "";
  
  @Note("缓存时间，0表示缓存服务的默认时间")
  int seconds() default 0;
  
  @Note("缓存唯一标识，不能有逗号")
  String key() default "";
  
  @Note("缓存标签，多个以逗号隔开")
  String tags() default "";
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\annotation\CachePut.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */