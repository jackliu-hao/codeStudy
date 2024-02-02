package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ConcurrentServerRunner<T extends Client> extends ContextAwareBase implements Runnable, ServerRunner<T> {
   private final Lock clientsLock = new ReentrantLock();
   private final Collection<T> clients = new ArrayList();
   private final ServerListener<T> listener;
   private final Executor executor;
   private boolean running;

   public ConcurrentServerRunner(ServerListener<T> listener, Executor executor) {
      this.listener = listener;
      this.executor = executor;
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void setRunning(boolean running) {
      this.running = running;
   }

   public void stop() throws IOException {
      this.listener.close();
      this.accept(new ClientVisitor<T>() {
         public void visit(T client) {
            client.close();
         }
      });
   }

   public void accept(ClientVisitor<T> visitor) {
      Collection<T> clients = this.copyClients();
      Iterator var3 = clients.iterator();

      while(var3.hasNext()) {
         T client = (Client)var3.next();

         try {
            visitor.visit(client);
         } catch (RuntimeException var6) {
            this.addError(client + ": " + var6);
         }
      }

   }

   private Collection<T> copyClients() {
      this.clientsLock.lock();

      ArrayList var2;
      try {
         Collection<T> copy = new ArrayList(this.clients);
         var2 = copy;
      } finally {
         this.clientsLock.unlock();
      }

      return var2;
   }

   public void run() {
      this.setRunning(true);

      try {
         this.addInfo("listening on " + this.listener);

         while(!Thread.currentThread().isInterrupted()) {
            T client = this.listener.acceptClient();
            if (!this.configureClient(client)) {
               this.addError(client + ": connection dropped");
               client.close();
            } else {
               try {
                  this.executor.execute(new ClientWrapper(client));
               } catch (RejectedExecutionException var3) {
                  this.addError(client + ": connection dropped");
                  client.close();
               }
            }
         }
      } catch (InterruptedException var4) {
      } catch (Exception var5) {
         this.addError("listener: " + var5);
      }

      this.setRunning(false);
      this.addInfo("shutting down");
      this.listener.close();
   }

   protected abstract boolean configureClient(T var1);

   private void addClient(T client) {
      this.clientsLock.lock();

      try {
         this.clients.add(client);
      } finally {
         this.clientsLock.unlock();
      }

   }

   private void removeClient(T client) {
      this.clientsLock.lock();

      try {
         this.clients.remove(client);
      } finally {
         this.clientsLock.unlock();
      }

   }

   private class ClientWrapper implements Client {
      private final T delegate;

      public ClientWrapper(T client) {
         this.delegate = client;
      }

      public void run() {
         ConcurrentServerRunner.this.addClient(this.delegate);

         try {
            this.delegate.run();
         } finally {
            ConcurrentServerRunner.this.removeClient(this.delegate);
         }

      }

      public void close() {
         this.delegate.close();
      }
   }
}
