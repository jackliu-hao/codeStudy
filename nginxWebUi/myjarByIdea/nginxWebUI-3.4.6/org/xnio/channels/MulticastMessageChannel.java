package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import org.xnio.ChannelListener;

public interface MulticastMessageChannel extends BoundMultipointMessageChannel {
   Key join(InetAddress var1, NetworkInterface var2) throws IOException;

   Key join(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException;

   ChannelListener.Setter<? extends MulticastMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends MulticastMessageChannel> getCloseSetter();

   ChannelListener.Setter<? extends MulticastMessageChannel> getWriteSetter();

   public interface Key extends Closeable {
      Key block(InetAddress var1) throws IOException, UnsupportedOperationException, IllegalStateException, IllegalArgumentException;

      Key unblock(InetAddress var1) throws IOException, IllegalStateException, UnsupportedOperationException;

      MulticastMessageChannel getChannel();

      InetAddress getGroup();

      NetworkInterface getNetworkInterface();

      InetAddress getSourceAddress();

      boolean isOpen();
   }
}
