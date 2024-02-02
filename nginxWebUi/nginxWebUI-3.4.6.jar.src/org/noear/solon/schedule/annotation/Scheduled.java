package org.noear.solon.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Note;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
  String name() default "";
  
  @Note("支持7位（秒，分，时，日期ofM，月，星期ofW，年）")
  String cron() default "";
  
  String zone() default "";
  
  long fixedRate() default 0L;
  
  long fixedDelay() default 0L;
  
  boolean concurrent() default false;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\annotation\Scheduled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */