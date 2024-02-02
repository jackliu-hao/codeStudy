package org.noear.solon.sessionstate.local;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateFactory;

public class LocalSessionStateFactory implements SessionStateFactory {
   private static LocalSessionStateFactory instance;
   public static final int SESSION_STATE_PRIORITY = 1;

   public static LocalSessionStateFactory getInstance() {
      if (instance == null) {
         instance = new LocalSessionStateFactory();
      }

      return instance;
   }

   private LocalSessionStateFactory() {
   }

   public int priority() {
      return 1;
   }

   public SessionState create(Context ctx) {
      return new LocalSessionState(ctx);
   }
}
