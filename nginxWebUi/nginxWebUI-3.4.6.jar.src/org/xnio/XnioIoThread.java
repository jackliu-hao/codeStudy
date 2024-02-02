/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.AssembledStreamChannel;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.StreamChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class XnioIoThread
/*     */   extends Thread
/*     */   implements XnioExecutor, XnioIoFactory
/*     */ {
/*     */   private final XnioWorker worker;
/*     */   private final int number;
/*     */   
/*     */   protected XnioIoThread(XnioWorker worker, int number) {
/*  53 */     this.number = number;
/*  54 */     this.worker = worker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XnioIoThread(XnioWorker worker, int number, String name) {
/*  65 */     super(name);
/*  66 */     this.number = number;
/*  67 */     this.worker = worker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XnioIoThread(XnioWorker worker, int number, ThreadGroup group, String name) {
/*  79 */     super(group, name);
/*  80 */     this.number = number;
/*  81 */     this.worker = worker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XnioIoThread(XnioWorker worker, int number, ThreadGroup group, String name, long stackSize) {
/*  94 */     super(group, null, name, stackSize);
/*  95 */     this.number = number;
/*  96 */     this.worker = worker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static XnioIoThread currentThread() {
/* 105 */     Thread thread = Thread.currentThread();
/* 106 */     if (thread instanceof XnioIoThread) {
/* 107 */       return (XnioIoThread)thread;
/*     */     }
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static XnioIoThread requireCurrentThread() throws IllegalStateException {
/* 121 */     XnioIoThread thread = currentThread();
/* 122 */     if (thread == null) {
/* 123 */       throw Messages.msg.xnioThreadRequired();
/*     */     }
/* 125 */     return thread;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumber() {
/* 134 */     return this.number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 143 */     return this.worker;
/*     */   }
/*     */   
/*     */   public IoFuture<StreamConnection> acceptStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 147 */     if (destination == null) {
/* 148 */       throw Messages.msg.nullParameter("destination");
/*     */     }
/* 150 */     if (destination instanceof InetSocketAddress)
/* 151 */       return acceptTcpStreamConnection((InetSocketAddress)destination, openListener, bindListener, optionMap); 
/* 152 */     if (destination instanceof LocalSocketAddress) {
/* 153 */       return acceptLocalStreamConnection((LocalSocketAddress)destination, openListener, bindListener, optionMap);
/*     */     }
/* 155 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<StreamConnection> acceptLocalStreamConnection(LocalSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 170 */     throw Messages.msg.unsupported("acceptLocalStreamConnection");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<StreamConnection> acceptTcpStreamConnection(InetSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 184 */     throw Messages.msg.unsupported("acceptTcpStreamConnection");
/*     */   }
/*     */   
/*     */   public IoFuture<MessageConnection> openMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
/* 188 */     if (destination == null) {
/* 189 */       throw Messages.msg.nullParameter("destination");
/*     */     }
/* 191 */     if (destination instanceof LocalSocketAddress) {
/* 192 */       return openLocalMessageConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, optionMap);
/*     */     }
/* 194 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<MessageConnection> acceptMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 199 */     if (destination == null) {
/* 200 */       throw Messages.msg.nullParameter("destination");
/*     */     }
/* 202 */     if (destination instanceof LocalSocketAddress) {
/* 203 */       return acceptLocalMessageConnection((LocalSocketAddress)destination, openListener, bindListener, optionMap);
/*     */     }
/* 205 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<MessageConnection> acceptLocalMessageConnection(LocalSocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 220 */     throw Messages.msg.unsupported("acceptLocalMessageConnection");
/*     */   }
/*     */   
/*     */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, OptionMap optionMap) {
/* 224 */     Assert.checkNotNullParam("destination", destination);
/* 225 */     if (destination instanceof InetSocketAddress)
/* 226 */       return internalOpenTcpStreamConnection((InetSocketAddress)destination, openListener, null, optionMap); 
/* 227 */     if (destination instanceof LocalSocketAddress) {
/* 228 */       return openLocalStreamConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, null, optionMap);
/*     */     }
/* 230 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 235 */     Assert.checkNotNullParam("destination", destination);
/* 236 */     if (destination instanceof InetSocketAddress)
/* 237 */       return internalOpenTcpStreamConnection((InetSocketAddress)destination, openListener, bindListener, optionMap); 
/* 238 */     if (destination instanceof LocalSocketAddress) {
/* 239 */       return openLocalStreamConnection(Xnio.ANY_LOCAL_ADDRESS, (LocalSocketAddress)destination, openListener, bindListener, optionMap);
/*     */     }
/* 241 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   private IoFuture<StreamConnection> internalOpenTcpStreamConnection(InetSocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 246 */     if (destination.isUnresolved()) {
/*     */       try {
/* 248 */         destination = new InetSocketAddress(InetAddress.getByName(destination.getHostString()), destination.getPort());
/* 249 */       } catch (UnknownHostException e) {
/* 250 */         return new FailedIoFuture<>(e);
/*     */       } 
/*     */     }
/* 253 */     InetSocketAddress bindAddress = (InetSocketAddress)getWorker().getBindAddressTable().get(destination.getAddress());
/* 254 */     return openTcpStreamConnection(bindAddress, destination, openListener, bindListener, optionMap);
/*     */   }
/*     */   
/*     */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 258 */     Assert.checkNotNullParam("bindAddress", bindAddress);
/* 259 */     Assert.checkNotNullParam("destination", destination);
/* 260 */     if (bindAddress.getClass() != destination.getClass()) {
/* 261 */       throw Messages.msg.mismatchSockType(bindAddress.getClass(), destination.getClass());
/*     */     }
/* 263 */     if (destination instanceof InetSocketAddress)
/* 264 */       return openTcpStreamConnection((InetSocketAddress)bindAddress, (InetSocketAddress)destination, openListener, bindListener, optionMap); 
/* 265 */     if (destination instanceof LocalSocketAddress) {
/* 266 */       return openLocalStreamConnection((LocalSocketAddress)bindAddress, (LocalSocketAddress)destination, openListener, bindListener, optionMap);
/*     */     }
/* 268 */     throw Messages.msg.badSockType(destination.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<StreamConnection> openTcpStreamConnection(InetSocketAddress bindAddress, InetSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 283 */     throw Messages.msg.unsupported("openTcpStreamConnection");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<StreamConnection> openLocalStreamConnection(LocalSocketAddress bindAddress, LocalSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 297 */     throw Messages.msg.unsupported("openLocalStreamConnection");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IoFuture<MessageConnection> openLocalMessageConnection(LocalSocketAddress bindAddress, LocalSocketAddress destinationAddress, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
/* 310 */     throw Messages.msg.unsupported("openLocalMessageConnection");
/*     */   }
/*     */   
/*     */   public ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException {
/* 314 */     ChannelPipe<StreamConnection, StreamConnection> connection = createFullDuplexPipeConnection();
/* 315 */     AssembledStreamChannel assembledStreamChannel1 = new AssembledStreamChannel(connection.getLeftSide(), (StreamSourceChannel)((StreamConnection)connection.getLeftSide()).getSourceChannel(), (StreamSinkChannel)((StreamConnection)connection.getLeftSide()).getSinkChannel());
/* 316 */     AssembledStreamChannel assembledStreamChannel2 = new AssembledStreamChannel(connection.getRightSide(), (StreamSourceChannel)((StreamConnection)connection.getRightSide()).getSourceChannel(), (StreamSinkChannel)((StreamConnection)connection.getRightSide()).getSinkChannel());
/* 317 */     return (ChannelPipe)new ChannelPipe<>(assembledStreamChannel1, assembledStreamChannel2);
/*     */   }
/*     */   
/*     */   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException {
/* 321 */     return createFullDuplexPipeConnection(this);
/*     */   }
/*     */   
/*     */   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException {
/* 325 */     return createHalfDuplexPipe(this);
/*     */   }
/*     */   
/*     */   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException {
/* 329 */     throw Messages.msg.unsupported("createFullDuplexPipeConnection");
/*     */   }
/*     */   
/*     */   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException {
/* 333 */     throw Messages.msg.unsupported("createHalfDuplexPipe");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioIoThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */