package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareImpl;
import org.xml.sax.Locator;

class CAI_WithLocatorSupport extends ContextAwareImpl {
   CAI_WithLocatorSupport(Context context, Interpreter interpreter) {
      super(context, interpreter);
   }

   protected Object getOrigin() {
      Interpreter i = (Interpreter)super.getOrigin();
      Locator locator = i.locator;
      return locator != null ? Interpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber() : Interpreter.class.getName() + "@NA:NA";
   }
}
