package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.socket.SocketRuntimeException;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient implements Closeable {
   private Selector selector;
   private SocketChannel channel;
   private ChannelHandler handler;

   public NioClient(String host, int port) {
      this.init(new InetSocketAddress(host, port));
   }

   public NioClient(InetSocketAddress address) {
      this.init(address);
   }

   public NioClient init(InetSocketAddress address) {
      try {
         this.channel = SocketChannel.open();
         this.channel.configureBlocking(false);
         this.channel.connect(address);
         this.selector = Selector.open();
         this.channel.register(this.selector, 1);

         while(!this.channel.finishConnect()) {
         }

         return this;
      } catch (IOException var3) {
         this.close();
         throw new IORuntimeException(var3);
      }
   }

   public NioClient setChannelHandler(ChannelHandler handler) {
      this.handler = handler;
      return this;
   }

   public void listen() {
      ThreadUtil.execute(() -> {
         try {
            this.doListen();
         } catch (IOException var2) {
            var2.printStackTrace();
         }

      });
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
      if (key.isReadable()) {
         SocketChannel socketChannel = (SocketChannel)key.channel();

         try {
            this.handler.handle(socketChannel);
         } catch (Exception var4) {
            throw new SocketRuntimeException(var4);
         }
      }

   }

   public NioClient write(ByteBuffer... datas) {
      try {
         this.channel.write(datas);
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public SocketChannel getChannel() {
      return this.channel;
   }

   public void close() {
      IoUtil.close(this.selector);
      IoUtil.close(this.channel);
   }
}
