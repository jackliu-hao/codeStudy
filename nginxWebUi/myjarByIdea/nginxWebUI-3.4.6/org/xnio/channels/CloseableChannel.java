package org.xnio.channels;

import java.io.IOException;
import java.nio.channels.InterruptibleChannel;
import org.xnio.ChannelListener;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public interface CloseableChannel extends InterruptibleChannel, Configurable {
   ChannelListener.Setter<? extends CloseableChannel> getCloseSetter();

   XnioWorker getWorker();

   XnioIoThread getIoThread();

   void close() throws IOException;
}
