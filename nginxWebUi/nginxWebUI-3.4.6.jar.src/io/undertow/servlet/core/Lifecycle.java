package io.undertow.servlet.core;

import javax.servlet.ServletException;

public interface Lifecycle {
  void start() throws ServletException;
  
  void stop() throws ServletException;
  
  boolean isStarted();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\Lifecycle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */