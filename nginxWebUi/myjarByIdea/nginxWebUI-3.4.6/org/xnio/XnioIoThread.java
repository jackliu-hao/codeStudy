package org.xnio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import org.wildfly.common.Assert;
import org.xnio._private.Messages;
import org.xnio.channels.AssembledStreamChannel;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.StreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public abstract class XnioIoThread extends Thread implements XnioExecutor, XnioIoFactory {
   private final XnioWorker worker;
   private final int number;

   protected XnioIoThread(XnioWorker worker, int number) {
      this.number = number;
      this.worker = worker;
   }

   protected XnioIoThread(XnioWorker worker, int number, String name) {
      super(name);
      this.number = number;
      this.worker = worker;
   }

   protected XnioIoThread(XnioWorker worker, int number, ThreadGroup group, String name) {
      super(group, name);
      this.number = number;
      this.worker = worker;
   }

   protected XnioIoThread(XnioWorker worker, int number, ThreadGroup group, String name, long stackSize) {
      super(group, (Runnable)null, name, stackSize);
      this.number = number;
      this.worker = worker;
   }

   public static XnioIoThread currentThread() {
      Thread thread = Thread.currentThread();
      return thread instanceof XnioIoThread ? (XnioIoThread)thread : null;
   }

   public static XnioIoThread requireCurrentThread() throws IllegalStateException {
      XnioIoThread thread = currentThread();
      if (thread == null) {
         throw Messages.msg.xnioThreadRequired();
      } else {
         return thread;
      }
   }

   public int getNumber() {
      return this.number;
   }

   public XnioWorker getWorker() {
      return this.worker;
   }

   public IoFuture<StreamConnection> acceptStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      if (destination == null) {
         throw Messages.msg.nullParameter("destination");
      } else if (destination instanceof InetSocketAddress) {
         return this.acceptTcpStreamConnection((InetSocketAddress)destination, openListener, bindListener, optionMap);
      } else if (destination instanceof LocalSocketAddress) {
         return this.acceptLocalStreamConnection((LocalSocketAddress)destination, openListener, bindListener, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   protected IoFuture<StreamConnection> acceptLocalStreamConnection(LocalSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("acceptLocalStreamConnection");
   }

   protected IoFuture<StreamConnection> acceptTcpStreamConnection(InetSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("acceptTcpStreamConnection");
   }

   public IoFuture<MessageConnection> openMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
      if (destination == null) {
         throw Messages.msg.nullParameter("destination");
      } else if (destination instanceof LocalSocketAddress) {
         return this.openLocalMessageConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   public IoFuture<MessageConnection> acceptMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      if (destination == null) {
         throw Messages.msg.nullParameter("destination");
      } else if (destination instanceof LocalSocketAddress) {
         return this.acceptLocalMessageConnection((LocalSocketAddress)destination, openListener, bindListener, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   protected IoFuture<MessageConnection> acceptLocalMessageConnection(LocalSocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("acceptLocalMessageConnection");
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, OptionMap optionMap) {
      Assert.checkNotNullParam("destination", destination);
      if (destination instanceof InetSocketAddress) {
         return this.internalOpenTcpStreamConnection((InetSocketAddress)destination, openListener, (ChannelListener)null, optionMap);
      } else if (destination instanceof LocalSocketAddress) {
         return this.openLocalStreamConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, (ChannelListener)null, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      Assert.checkNotNullParam("destination", destination);
      if (destination instanceof InetSocketAddress) {
         return this.internalOpenTcpStreamConnection((InetSocketAddress)destination, openListener, bindListener, optionMap);
      } else if (destination instanceof LocalSocketAddress) {
         return this.openLocalStreamConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, bindListener, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   private IoFuture<StreamConnection> internalOpenTcpStreamConnection(InetSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      if (destination.isUnresolved()) {
         try {
            destination = new InetSocketAddress(InetAddress.getByName(destination.getHostString()), destination.getPort());
         } catch (UnknownHostException var6) {
            return new FailedIoFuture(var6);
         }
      }

      InetSocketAddress bindAddress = (InetSocketAddress)this.getWorker().getBindAddressTable().get(destination.getAddress());
      return this.openTcpStreamConnection(bindAddress, destination, openListener, bindListener, optionMap);
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      Assert.checkNotNullParam("bindAddress", bindAddress);
      Assert.checkNotNullParam("destination", destination);
      if (bindAddress.getClass() != destination.getClass()) {
         throw Messages.msg.mismatchSockType(bindAddress.getClass(), destination.getClass());
      } else if (destination instanceof InetSocketAddress) {
         return this.openTcpStreamConnection((InetSocketAddress)bindAddress, (InetSocketAddress)destination, openListener, bindListener, optionMap);
      } else if (destination instanceof LocalSocketAddress) {
         return this.openLocalStreamConnection((LocalSocketAddress)bindAddress, (LocalSocketAddress)destination, openListener, bindListener, optionMap);
      } else {
         throw Messages.msg.badSockType(destination.getClass());
      }
   }

   protected IoFuture<StreamConnection> openTcpStreamConnection(InetSocketAddress bindAddress, InetSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("openTcpStreamConnection");
   }

   protected IoFuture<StreamConnection> openLocalStreamConnection(LocalSocketAddress bindAddress, LocalSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("openLocalStreamConnection");
   }

   protected IoFuture<MessageConnection> openLocalMessageConnection(LocalSocketAddress bindAddress, LocalSocketAddress destinationAddress, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
      throw Messages.msg.unsupported("openLocalMessageConnection");
   }

   public ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException {
      ChannelPipe<StreamConnection, StreamConnection> connection = this.createFullDuplexPipeConnection();
      StreamChannel left = new AssembledStreamChannel(connection.getLeftSide(), ((StreamConnection)connection.getLeftSide()).getSourceChannel(), ((StreamConnection)connection.getLeftSide()).getSinkChannel());
      StreamChannel right = new AssembledStreamChannel(connection.getRightSide(), ((StreamConnection)connection.getRightSide()).getSourceChannel(), ((StreamConnection)connection.getRightSide()).getSinkChannel());
      return new ChannelPipe(left, right);
   }

   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException {
      return this.createFullDuplexPipeConnection(this);
   }

   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException {
      return this.createHalfDuplexPipe(this);
   }

   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException {
      throw Messages.msg.unsupported("createFullDuplexPipeConnection");
   }

   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException {
      throw Messages.msg.unsupported("createHalfDuplexPipe");
   }
}
