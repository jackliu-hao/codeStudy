package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.CloseUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

class RemoteReceiverStreamClient extends ContextAwareBase implements RemoteReceiverClient {
   private final String clientId;
   private final Socket socket;
   private final OutputStream outputStream;
   private BlockingQueue<Serializable> queue;

   public RemoteReceiverStreamClient(String id, Socket socket) {
      this.clientId = "client " + id + ": ";
      this.socket = socket;
      this.outputStream = null;
   }

   RemoteReceiverStreamClient(String id, OutputStream outputStream) {
      this.clientId = "client " + id + ": ";
      this.socket = null;
      this.outputStream = outputStream;
   }

   public void setQueue(BlockingQueue<Serializable> queue) {
      this.queue = queue;
   }

   public boolean offer(Serializable event) {
      if (this.queue == null) {
         throw new IllegalStateException("client has no event queue");
      } else {
         return this.queue.offer(event);
      }
   }

   public void close() {
      if (this.socket != null) {
         CloseUtil.closeQuietly(this.socket);
      }
   }

   public void run() {
      this.addInfo(this.clientId + "connected");
      ObjectOutputStream oos = null;

      try {
         int counter = 0;
         oos = this.createObjectOutputStream();

         while(!Thread.currentThread().isInterrupted()) {
            try {
               Serializable event = (Serializable)this.queue.take();
               oos.writeObject(event);
               oos.flush();
               ++counter;
               if (counter >= 70) {
                  counter = 0;
                  oos.reset();
               }
            } catch (InterruptedException var10) {
               Thread.currentThread().interrupt();
            }
         }
      } catch (SocketException var11) {
         this.addInfo(this.clientId + var11);
      } catch (IOException var12) {
         this.addError(this.clientId + var12);
      } catch (RuntimeException var13) {
         this.addError(this.clientId + var13);
      } finally {
         if (oos != null) {
            CloseUtil.closeQuietly((Closeable)oos);
         }

         this.close();
         this.addInfo(this.clientId + "connection closed");
      }

   }

   private ObjectOutputStream createObjectOutputStream() throws IOException {
      return this.socket == null ? new ObjectOutputStream(this.outputStream) : new ObjectOutputStream(this.socket.getOutputStream());
   }
}
