package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ProxyClient {
   ProxyTarget findTarget(HttpServerExchange var1);

   void getConnection(ProxyTarget var1, HttpServerExchange var2, ProxyCallback<ProxyConnection> var3, long var4, TimeUnit var6);

   default List<ProxyTarget> getAllTargets() {
      return new ArrayList();
   }

   public interface HostProxyTarget extends ProxyTarget {
      void setHost(LoadBalancingProxyClient.Host var1);
   }

   public interface MaxRetriesProxyTarget extends ProxyTarget {
      int getMaxRetries();
   }

   public interface ProxyTarget {
   }
}
