package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.StaticLog;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer implements Closeable {
   private static final Log log = Log.get();
   private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
   private Selector selector;
   private ServerSocketChannel serverSocketChannel;
   private ChannelHandler handler;

   public NioServer(int port) {
      this.init(new InetSocketAddress(port));
   }

   public NioServer init(InetSocketAddress address) {
      try {
         this.serverSocketChannel = ServerSocketChannel.open();
         this.serverSocketChannel.configureBlocking(false);
         this.serverSocketChannel.bind(address);
         this.selector = Selector.open();
         this.serverSocketChannel.register(this.selector, 16);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }

      log.debug("Server listen on: [{}]...", new Object[]{address});
      return this;
   }

   public NioServer setChannelHandler(ChannelHandler handler) {
      this.handler = handler;
      return this;
   }

   public Selector getSelector() {
      return this.selector;
   }

   public void start() {
      this.listen();
   }

   public void listen() {
      try {
         this.doListen();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   private void doListen() throws IOException {
      label18:
      while(true) {
         if (this.selector.isOpen() && 0 != this.selector.select()) {
            Iterator<SelectionKey> keyIter = this.selector.selectedKeys().iterator();

            while(true) {
               if (!keyIter.hasNext()) {
                  continue label18;
               }

               this.handle((SelectionKey)keyIter.next());
               keyIter.remove();
            }
         }

         return;
      }
   }

   private void handle(SelectionKey key) {
      if (key.isAcceptable()) {
         ACCEPT_HANDLER.completed((ServerSocketChannel)key.channel(), this);
      }

      if (key.isReadable()) {
         SocketChannel socketChannel = (SocketChannel)key.channel();

         try {
            this.handler.handle(socketChannel);
         } catch (Exception var4) {
            IoUtil.close(socketChannel);
            StaticLog.error(var4);
         }
      }

   }

   public void close() {
      IoUtil.close(this.selector);
      IoUtil.close(this.serverSocketChannel);
   }
}
