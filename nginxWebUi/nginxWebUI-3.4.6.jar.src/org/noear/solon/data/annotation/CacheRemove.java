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
public @interface CacheRemove {
  @Note("缓存服务")
  String service() default "";
  
  @Note("缓存唯一标识，多个以逗号隔开")
  String keys() default "";
  
  @Note("清除缓存标签，多个以逗号隔开")
  String tags() default "";
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\annotation\CacheRemove.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */