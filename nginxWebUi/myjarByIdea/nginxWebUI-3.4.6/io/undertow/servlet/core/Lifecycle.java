package io.undertow.servlet.core;

import javax.servlet.ServletException;

public interface Lifecycle {
   void start() throws ServletException;

   void stop() throws ServletException;

   boolean isStarted();
}
