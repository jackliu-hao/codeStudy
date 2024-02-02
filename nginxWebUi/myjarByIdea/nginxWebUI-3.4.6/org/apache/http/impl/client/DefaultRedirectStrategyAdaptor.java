package org.apache.http.impl.client;

import java.net.URI;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
class DefaultRedirectStrategyAdaptor implements RedirectStrategy {
   private final RedirectHandler handler;

   public DefaultRedirectStrategyAdaptor(RedirectHandler handler) {
      this.handler = handler;
   }

   public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
      return this.handler.isRedirectRequested(response, context);
   }

   public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
      URI uri = this.handler.getLocationURI(response, context);
      String method = request.getRequestLine().getMethod();
      return (HttpUriRequest)(method.equalsIgnoreCase("HEAD") ? new HttpHead(uri) : new HttpGet(uri));
   }

   public RedirectHandler getHandler() {
      return this.handler;
   }
}
