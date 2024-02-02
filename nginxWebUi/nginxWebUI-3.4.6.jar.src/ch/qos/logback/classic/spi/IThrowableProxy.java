package ch.qos.logback.classic.spi;

public interface IThrowableProxy {
  String getMessage();
  
  String getClassName();
  
  StackTraceElementProxy[] getStackTraceElementProxyArray();
  
  int getCommonFrames();
  
  IThrowableProxy getCause();
  
  IThrowableProxy[] getSuppressed();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\IThrowableProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */