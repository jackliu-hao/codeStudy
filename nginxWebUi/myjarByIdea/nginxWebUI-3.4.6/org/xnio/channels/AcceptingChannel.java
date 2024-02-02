package org.xnio.channels;

import java.io.IOException;
import org.xnio.ChannelListener;

public interface AcceptingChannel<C extends ConnectedChannel> extends BoundChannel, SimpleAcceptingChannel<C> {
   C accept() throws IOException;

   ChannelListener.Setter<? extends AcceptingChannel<C>> getAcceptSetter();

   ChannelListener.Setter<? extends AcceptingChannel<C>> getCloseSetter();
}
