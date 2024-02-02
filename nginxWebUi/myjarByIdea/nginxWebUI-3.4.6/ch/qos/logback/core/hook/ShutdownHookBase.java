package ch.qos.logback.core.hook;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class ShutdownHookBase extends ContextAwareBase implements ShutdownHook {
   protected void stop() {
      this.addInfo("Logback context being closed via shutdown hook");
      Context hookContext = this.getContext();
      if (hookContext instanceof ContextBase) {
         ContextBase context = (ContextBase)hookContext;
         context.stop();
      }

   }
}
