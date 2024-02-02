package org.noear.solon.core.message;

import java.io.IOException;

public abstract class ListenerEmpty implements Listener {
   public void onMessage(Session session, Message message) throws IOException {
   }
}
