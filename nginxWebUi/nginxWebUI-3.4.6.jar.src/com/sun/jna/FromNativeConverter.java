package com.sun.jna;

public interface FromNativeConverter {
  Object fromNative(Object paramObject, FromNativeContext paramFromNativeContext);
  
  Class<?> nativeType();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\FromNativeConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */