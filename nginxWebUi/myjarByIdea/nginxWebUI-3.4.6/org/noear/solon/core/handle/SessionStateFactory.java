package org.noear.solon.core.handle;

public interface SessionStateFactory {
   default int priority() {
      return 0;
   }

   SessionState create(Context ctx);
}
