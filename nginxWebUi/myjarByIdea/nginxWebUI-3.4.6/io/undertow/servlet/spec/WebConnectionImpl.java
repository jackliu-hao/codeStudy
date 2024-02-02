package io.undertow.servlet.spec;

import io.undertow.connector.ByteBufferPool;
import java.io.IOException;
import java.util.concurrent.Executor;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.WebConnection;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;

public class WebConnectionImpl implements WebConnection {
   private final StreamConnection channel;
   private final UpgradeServletOutputStream outputStream;
   private final UpgradeServletInputStream inputStream;
   private final Executor ioExecutor;

   public WebConnectionImpl(StreamConnection channel, ByteBufferPool bufferPool, Executor ioExecutor) {
      this.channel = channel;
      this.ioExecutor = ioExecutor;
      this.outputStream = new UpgradeServletOutputStream(channel.getSinkChannel(), ioExecutor);
      this.inputStream = new UpgradeServletInputStream(channel.getSourceChannel(), bufferPool, ioExecutor);
      channel.getCloseSetter().set(new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection channel) {
            try {
               WebConnectionImpl.this.close();
            } catch (Exception var3) {
               throw new RuntimeException(var3);
            }
         }
      });
   }

   public ServletInputStream getInputStream() throws IOException {
      return this.inputStream;
   }

   public ServletOutputStream getOutputStream() throws IOException {
      return this.outputStream;
   }

   public void close() throws Exception {
      try {
         this.outputStream.closeBlocking();
      } finally {
         IoUtils.safeClose(this.inputStream, this.channel);
      }

   }
}
