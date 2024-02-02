package javax.servlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartConfig {
  String location() default "";
  
  long maxFileSize() default -1L;
  
  long maxRequestSize() default -1L;
  
  int fileSizeThreshold() default 0;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\annotation\MultipartConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */