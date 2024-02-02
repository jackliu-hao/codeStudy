package io.undertow.servlet.core;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.spec.WebConnectionImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import javax.servlet.http.HttpUpgradeHandler;
import org.xnio.ChannelListener;
import org.xnio.StreamConnection;

public class ServletUpgradeListener<T extends HttpUpgradeHandler> implements HttpUpgradeListener {
   private final HttpServerExchange exchange;
   private final ThreadSetupHandler.Action<Void, StreamConnection> initAction;
   private final ThreadSetupHandler.Action<Void, Object> destroyAction;

   public ServletUpgradeListener(final InstanceHandle<T> instance, Deployment deployment, HttpServerExchange exchange) {
      this.exchange = exchange;
      this.initAction = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, StreamConnection>() {
         public Void call(HttpServerExchange exchange, StreamConnection context) {
            DelayedExecutor executor = new DelayedExecutor(exchange.getIoThread());

            try {
               ((HttpUpgradeHandler)instance.getInstance()).init(new WebConnectionImpl(context, ServletUpgradeListener.this.exchange.getConnection().getByteBufferPool(), executor));
            } finally {
               executor.openGate();
            }

            return null;
         }
      });
      this.destroyAction = new ThreadSetupHandler.Action<Void, Object>() {
         public Void call(HttpServerExchange exchange, Object context) throws Exception {
            try {
               ((HttpUpgradeHandler)instance.getInstance()).destroy();
            } finally {
               instance.release();
            }

            return null;
         }
      };
   }

   public void handleUpgrade(final StreamConnection channel, final HttpServerExchange exchange) {
      channel.getCloseSetter().set(new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection channel) {
            try {
               ServletUpgradeListener.this.destroyAction.call((HttpServerExchange)null, (Object)null);
            } catch (Exception var3) {
               throw new RuntimeException(var3);
            }
         }
      });
      this.exchange.getConnection().getWorker().execute(new Runnable() {
         public void run() {
            try {
               ServletUpgradeListener.this.initAction.call(exchange, channel);
            } catch (Exception var2) {
               throw new RuntimeException(var2);
            }
         }
      });
   }

   private static final class DelayedExecutor implements Executor {
      private final Executor delegate;
      private volatile boolean queue;
      private final List<Runnable> tasks;

      private DelayedExecutor(Executor delegate) {
         this.queue = true;
         this.tasks = new ArrayList();
         this.delegate = delegate;
      }

      public void execute(Runnable command) {
         if (!this.queue) {
            this.delegate.execute(command);
         } else {
            synchronized(this) {
               if (!this.queue) {
                  this.delegate.execute(command);
               } else {
                  this.tasks.add(command);
               }
            }
         }

      }

      synchronized void openGate() {
         this.queue = false;
         Iterator var1 = this.tasks.iterator();

         while(var1.hasNext()) {
            Runnable task = (Runnable)var1.next();
            this.delegate.execute(task);
         }

      }

      // $FF: synthetic method
      DelayedExecutor(Executor x0, Object x1) {
         this(x0);
      }
   }
}
