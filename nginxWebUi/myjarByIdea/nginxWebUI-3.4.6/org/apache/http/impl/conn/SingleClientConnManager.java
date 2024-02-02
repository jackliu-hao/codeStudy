package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SingleClientConnManager implements ClientConnectionManager {
   private final Log log;
   public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
   protected final SchemeRegistry schemeRegistry;
   protected final ClientConnectionOperator connOperator;
   protected final boolean alwaysShutDown;
   protected volatile PoolEntry uniquePoolEntry;
   protected volatile ConnAdapter managedConn;
   protected volatile long lastReleaseTime;
   protected volatile long connectionExpiresTime;
   protected volatile boolean isShutDown;

   /** @deprecated */
   @Deprecated
   public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
      this(schreg);
   }

   public SingleClientConnManager(SchemeRegistry schreg) {
      this.log = LogFactory.getLog(this.getClass());
      Args.notNull(schreg, "Scheme registry");
      this.schemeRegistry = schreg;
      this.connOperator = this.createConnectionOperator(schreg);
      this.uniquePoolEntry = new PoolEntry();
      this.managedConn = null;
      this.lastReleaseTime = -1L;
      this.alwaysShutDown = false;
      this.isShutDown = false;
   }

   public SingleClientConnManager() {
      this(SchemeRegistryFactory.createDefault());
   }

   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }

   }

   public SchemeRegistry getSchemeRegistry() {
      return this.schemeRegistry;
   }

   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
      return new DefaultClientConnectionOperator(schreg);
   }

   protected final void assertStillUp() throws IllegalStateException {
      Asserts.check(!this.isShutDown, "Manager is shut down");
   }

   public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
      return new ClientConnectionRequest() {
         public void abortRequest() {
         }

         public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) {
            return SingleClientConnManager.this.getConnection(route, state);
         }
      };
   }

   public ManagedClientConnection getConnection(HttpRoute route, Object state) {
      Args.notNull(route, "Route");
      this.assertStillUp();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Get connection for route " + route);
      }

      synchronized(this) {
         Asserts.check(this.managedConn == null, "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
         boolean recreate = false;
         boolean shutdown = false;
         this.closeExpiredConnections();
         if (this.uniquePoolEntry.connection.isOpen()) {
            RouteTracker tracker = this.uniquePoolEntry.tracker;
            shutdown = tracker == null || !tracker.toRoute().equals(route);
         } else {
            recreate = true;
         }

         if (shutdown) {
            recreate = true;

            try {
               this.uniquePoolEntry.shutdown();
            } catch (IOException var8) {
               this.log.debug("Problem shutting down connection.", var8);
            }
         }

         if (recreate) {
            this.uniquePoolEntry = new PoolEntry();
         }

         this.managedConn = new ConnAdapter(this.uniquePoolEntry, route);
         return this.managedConn;
      }
   }

   public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
      Args.check(conn instanceof ConnAdapter, "Connection class mismatch, connection not obtained from this manager");
      this.assertStillUp();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Releasing connection " + conn);
      }

      ConnAdapter sca = (ConnAdapter)conn;
      synchronized(sca) {
         if (sca.poolEntry != null) {
            ClientConnectionManager manager = sca.getManager();
            Asserts.check(manager == this, "Connection not obtained from this manager");
            boolean var21 = false;

            label200: {
               try {
                  var21 = true;
                  if (sca.isOpen()) {
                     if (!this.alwaysShutDown && sca.isMarkedReusable()) {
                        var21 = false;
                     } else {
                        if (this.log.isDebugEnabled()) {
                           this.log.debug("Released connection open but not reusable.");
                        }

                        sca.shutdown();
                        var21 = false;
                     }
                  } else {
                     var21 = false;
                  }
                  break label200;
               } catch (IOException var25) {
                  if (this.log.isDebugEnabled()) {
                     this.log.debug("Exception shutting down released connection.", var25);
                     var21 = false;
                  } else {
                     var21 = false;
                  }
               } finally {
                  if (var21) {
                     sca.detach();
                     synchronized(this) {
                        this.managedConn = null;
                        this.lastReleaseTime = System.currentTimeMillis();
                        if (validDuration > 0L) {
                           this.connectionExpiresTime = timeUnit.toMillis(validDuration) + this.lastReleaseTime;
                        } else {
                           this.connectionExpiresTime = Long.MAX_VALUE;
                        }

                     }
                  }
               }

               sca.detach();
               synchronized(this) {
                  this.managedConn = null;
                  this.lastReleaseTime = System.currentTimeMillis();
                  if (validDuration > 0L) {
                     this.connectionExpiresTime = timeUnit.toMillis(validDuration) + this.lastReleaseTime;
                  } else {
                     this.connectionExpiresTime = Long.MAX_VALUE;
                  }

                  return;
               }
            }

            sca.detach();
            synchronized(this) {
               this.managedConn = null;
               this.lastReleaseTime = System.currentTimeMillis();
               if (validDuration > 0L) {
                  this.connectionExpiresTime = timeUnit.toMillis(validDuration) + this.lastReleaseTime;
               } else {
                  this.connectionExpiresTime = Long.MAX_VALUE;
               }
            }

         }
      }
   }

   public void closeExpiredConnections() {
      long time = this.connectionExpiresTime;
      if (System.currentTimeMillis() >= time) {
         this.closeIdleConnections(0L, TimeUnit.MILLISECONDS);
      }

   }

   public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
      this.assertStillUp();
      Args.notNull(timeUnit, "Time unit");
      synchronized(this) {
         if (this.managedConn == null && this.uniquePoolEntry.connection.isOpen()) {
            long cutoff = System.currentTimeMillis() - timeUnit.toMillis(idletime);
            if (this.lastReleaseTime <= cutoff) {
               try {
                  this.uniquePoolEntry.close();
               } catch (IOException var9) {
                  this.log.debug("Problem closing idle connection.", var9);
               }
            }
         }

      }
   }

   public void shutdown() {
      this.isShutDown = true;
      synchronized(this) {
         try {
            if (this.uniquePoolEntry != null) {
               this.uniquePoolEntry.shutdown();
            }
         } catch (IOException var8) {
            this.log.debug("Problem while shutting down manager.", var8);
         } finally {
            this.uniquePoolEntry = null;
            this.managedConn = null;
         }

      }
   }

   protected void revokeConnection() {
      ConnAdapter conn = this.managedConn;
      if (conn != null) {
         conn.detach();
         synchronized(this) {
            try {
               this.uniquePoolEntry.shutdown();
            } catch (IOException var5) {
               this.log.debug("Problem while shutting down connection.", var5);
            }

         }
      }
   }

   protected class ConnAdapter extends AbstractPooledConnAdapter {
      protected ConnAdapter(PoolEntry entry, HttpRoute route) {
         super(SingleClientConnManager.this, entry);
         this.markReusable();
         entry.route = route;
      }
   }

   protected class PoolEntry extends AbstractPoolEntry {
      protected PoolEntry() {
         super(SingleClientConnManager.this.connOperator, (HttpRoute)null);
      }

      protected void close() throws IOException {
         this.shutdownEntry();
         if (this.connection.isOpen()) {
            this.connection.close();
         }

      }

      protected void shutdown() throws IOException {
         this.shutdownEntry();
         if (this.connection.isOpen()) {
            this.connection.shutdown();
         }

      }
   }
}
