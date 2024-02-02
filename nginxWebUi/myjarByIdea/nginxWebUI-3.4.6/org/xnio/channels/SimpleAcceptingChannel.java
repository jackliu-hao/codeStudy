package org.xnio.channels;

import java.io.IOException;
import org.xnio.ChannelListener;

public interface SimpleAcceptingChannel<C extends CloseableChannel> extends SuspendableAcceptChannel {
   C accept() throws IOException;

   ChannelListener.Setter<? extends SimpleAcceptingChannel<C>> getAcceptSetter();

   ChannelListener.Setter<? extends SimpleAcceptingChannel<C>> getCloseSetter();
}
