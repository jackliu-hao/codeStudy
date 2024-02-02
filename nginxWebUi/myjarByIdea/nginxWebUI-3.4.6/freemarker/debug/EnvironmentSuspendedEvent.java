package freemarker.debug;

import java.util.EventObject;

public class EnvironmentSuspendedEvent extends EventObject {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final int line;
   private final DebuggedEnvironment env;

   public EnvironmentSuspendedEvent(Object source, String templateName, int line, DebuggedEnvironment env) {
      super(source);
      this.name = templateName;
      this.line = line;
      this.env = env;
   }

   public String getName() {
      return this.name;
   }

   public int getLine() {
      return this.line;
   }

   public DebuggedEnvironment getEnvironment() {
      return this.env;
   }
}
