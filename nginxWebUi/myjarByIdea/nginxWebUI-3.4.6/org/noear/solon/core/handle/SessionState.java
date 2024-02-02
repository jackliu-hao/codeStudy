package org.noear.solon.core.handle;

import java.util.Collection;

public interface SessionState {
   default void sessionRefresh() {
   }

   default void sessionPublish() {
   }

   default boolean replaceable() {
      return true;
   }

   String sessionId();

   String sessionChangeId();

   Collection<String> sessionKeys();

   Object sessionGet(String key);

   void sessionSet(String key, Object val);

   void sessionRemove(String key);

   void sessionClear();

   void sessionReset();

   default String sessionToken() {
      throw new UnsupportedOperationException();
   }
}
