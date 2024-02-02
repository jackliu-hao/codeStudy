package ch.qos.logback.core.spi;

public interface LifeCycle {
   void start();

   void stop();

   boolean isStarted();
}
