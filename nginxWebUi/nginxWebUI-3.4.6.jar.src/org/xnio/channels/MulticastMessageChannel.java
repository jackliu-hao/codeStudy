package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import org.xnio.ChannelListener;

public interface MulticastMessageChannel extends BoundMultipointMessageChannel {
  Key join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface) throws IOException;
  
  Key join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2) throws IOException;
  
  ChannelListener.Setter<? extends MulticastMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends MulticastMessageChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends MulticastMessageChannel> getWriteSetter();
  
  public static interface Key extends Closeable {
    Key block(InetAddress param1InetAddress) throws IOException, UnsupportedOperationException, IllegalStateException, IllegalArgumentException;
    
    Key unblock(InetAddress param1InetAddress) throws IOException, IllegalStateException, UnsupportedOperationException;
    
    MulticastMessageChannel getChannel();
    
    InetAddress getGroup();
    
    NetworkInterface getNetworkInterface();
    
    InetAddress getSourceAddress();
    
    boolean isOpen();
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\MulticastMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */