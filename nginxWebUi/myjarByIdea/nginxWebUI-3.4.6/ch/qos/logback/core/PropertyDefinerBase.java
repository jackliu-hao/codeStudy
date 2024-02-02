package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyDefiner;

public abstract class PropertyDefinerBase extends ContextAwareBase implements PropertyDefiner {
   protected static String booleanAsStr(boolean bool) {
      return bool ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
   }
}
