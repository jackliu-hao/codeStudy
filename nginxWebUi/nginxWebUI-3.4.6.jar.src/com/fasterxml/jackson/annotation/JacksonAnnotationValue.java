package com.fasterxml.jackson.annotation;

public interface JacksonAnnotationValue<A extends java.lang.annotation.Annotation> {
  Class<A> valueFor();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JacksonAnnotationValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */