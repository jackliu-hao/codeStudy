package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;

class Context {
   private static final AtomicInteger idGen = new AtomicInteger();
   private final int id;
   private final Node node;
   private final String path;
   private final Node.VHostMapping vhost;
   private static final int STOPPED = Integer.MIN_VALUE;
   private static final int DISABLED = 1073741824;
   private static final int REQUEST_MASK = 1073741823;
   private volatile int state = Integer.MIN_VALUE;
   private static final AtomicIntegerFieldUpdater<Context> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Context.class, "state");

   Context(String path, Node.VHostMapping vHost, Node node) {
      this.id = idGen.incrementAndGet();
      this.path = path;
      this.node = node;
      this.vhost = vHost;
   }

   public int getId() {
      return this.id;
   }

   public String getJVMRoute() {
      return this.node.getJvmRoute();
   }

   public String getPath() {
      return this.path;
   }

   public List<String> getVirtualHosts() {
      return this.vhost.getAliases();
   }

   public int getActiveRequests() {
      return this.state & 1073741823;
   }

   public Status getStatus() {
      int state = this.state;
      if ((state & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
         return Context.Status.STOPPED;
      } else {
         return (state & 1073741824) == 1073741824 ? Context.Status.DISABLED : Context.Status.ENABLED;
      }
   }

   public boolean isEnabled() {
      return Bits.allAreClear(this.state, -1073741824);
   }

   public boolean isStopped() {
      return Bits.allAreSet(this.state, Integer.MIN_VALUE);
   }

   public boolean isDisabled() {
      return Bits.allAreSet(this.state, 1073741824);
   }

   Node getNode() {
      return this.node;
   }

   Node.VHostMapping getVhost() {
      return this.vhost;
   }

   boolean checkAvailable(boolean existingSession) {
      if (this.node.checkAvailable(existingSession)) {
         return existingSession ? !this.isStopped() : this.isEnabled();
      } else {
         return false;
      }
   }

   void enable() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState & 1073741823;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

   }

   void disable() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState | 1073741824;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

   }

   void stop() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState | Integer.MIN_VALUE;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

   }

   void handleRequest(ModClusterProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit, boolean exclusive) {
      if (this.addRequest()) {
         exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               Context.this.requestDone();
               nextListener.proceed();
            }
         });
         this.node.getConnectionPool().connect(target, exchange, callback, timeout, timeUnit, exclusive);
      } else {
         callback.failed(exchange);
      }

   }

   boolean addRequest() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         if ((oldState & Integer.MIN_VALUE) != 0) {
            return false;
         }

         newState = oldState + 1;
         if ((newState & 1073741823) == 1073741823) {
            return false;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      return true;
   }

   void requestDone() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         if ((oldState & 1073741823) == 0) {
            return;
         }

         newState = oldState - 1;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

   }

   public String toString() {
      return "Context{, path='" + this.path + '\'' + '}';
   }

   static enum Status {
      ENABLED,
      DISABLED,
      STOPPED;
   }
}
