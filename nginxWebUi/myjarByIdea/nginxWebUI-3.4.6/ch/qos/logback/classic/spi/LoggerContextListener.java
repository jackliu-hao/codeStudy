package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public interface LoggerContextListener {
   boolean isResetResistant();

   void onStart(LoggerContext var1);

   void onReset(LoggerContext var1);

   void onStop(LoggerContext var1);

   void onLevelChange(Logger var1, Level var2);
}
