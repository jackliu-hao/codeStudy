package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;

/** @deprecated */
@Deprecated
class HttpConnPool extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry> {
   private static final AtomicLong COUNTER = new AtomicLong();
   private final Log log;
   private final long timeToLive;
   private final TimeUnit timeUnit;

   public HttpConnPool(Log log, ClientConnectionOperator connOperator, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit timeUnit) {
      super(new InternalConnFactory(connOperator), defaultMaxPerRoute, maxTotal);
      this.log = log;
      this.timeToLive = timeToLive;
      this.timeUnit = timeUnit;
   }

   protected HttpPoolEntry createEntry(HttpRoute route, OperatedClientConnection conn) {
      String id = Long.toString(COUNTER.getAndIncrement());
      return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
   }

   static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection> {
      private final ClientConnectionOperator connOperator;

      InternalConnFactory(ClientConnectionOperator connOperator) {
         this.connOperator = connOperator;
      }

      public OperatedClientConnection create(HttpRoute route) throws IOException {
         return this.connOperator.createConnection();
      }
   }
}
