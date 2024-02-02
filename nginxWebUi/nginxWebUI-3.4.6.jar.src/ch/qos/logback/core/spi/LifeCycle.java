package ch.qos.logback.core.spi;

public interface LifeCycle {
  void start();
  
  void stop();
  
  boolean isStarted();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\LifeCycle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */