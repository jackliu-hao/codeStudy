package org.apache.http.impl.conn;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner {
   private final HttpHost proxy;

   public DefaultProxyRoutePlanner(HttpHost proxy, SchemePortResolver schemePortResolver) {
      super(schemePortResolver);
      this.proxy = (HttpHost)Args.notNull(proxy, "Proxy host");
   }

   public DefaultProxyRoutePlanner(HttpHost proxy) {
      this(proxy, (SchemePortResolver)null);
   }

   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
      return this.proxy;
   }
}
