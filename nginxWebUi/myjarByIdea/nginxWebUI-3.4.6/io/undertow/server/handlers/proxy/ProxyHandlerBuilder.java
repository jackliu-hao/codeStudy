package io.undertow.server.handlers.proxy;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProxyHandlerBuilder implements HandlerBuilder {
   public String name() {
      return "reverse-proxy";
   }

   public Map<String, Class<?>> parameters() {
      Map<String, Class<?>> params = new HashMap();
      params.put("hosts", String[].class);
      params.put("rewrite-host-header", Boolean.class);
      return params;
   }

   public Set<String> requiredParameters() {
      return Collections.singleton("hosts");
   }

   public String defaultParameter() {
      return "hosts";
   }

   public HandlerWrapper build(Map<String, Object> config) {
      String[] hosts = (String[])((String[])config.get("hosts"));
      List<URI> uris = new ArrayList();
      String[] var4 = hosts;
      int var5 = hosts.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String host = var4[var6];

         try {
            uris.add(new URI(host));
         } catch (URISyntaxException var9) {
            throw new RuntimeException(var9);
         }
      }

      Boolean rewriteHostHeader = (Boolean)config.get("rewrite-host-header");
      return new Wrapper(uris, rewriteHostHeader);
   }

   private static class Wrapper implements HandlerWrapper {
      private final List<URI> uris;
      private final boolean rewriteHostHeader;

      private Wrapper(List<URI> uris, Boolean rewriteHostHeader) {
         this.uris = uris;
         this.rewriteHostHeader = rewriteHostHeader != null && rewriteHostHeader;
      }

      public HttpHandler wrap(HttpHandler handler) {
         LoadBalancingProxyClient loadBalancingProxyClient = new LoadBalancingProxyClient();
         Iterator var3 = this.uris.iterator();

         while(var3.hasNext()) {
            URI url = (URI)var3.next();
            loadBalancingProxyClient.addHost(url);
         }

         return new ProxyHandler(loadBalancingProxyClient, -1, handler, this.rewriteHostHeader, false);
      }

      // $FF: synthetic method
      Wrapper(List x0, Boolean x1, Object x2) {
         this(x0, x1);
      }
   }
}
