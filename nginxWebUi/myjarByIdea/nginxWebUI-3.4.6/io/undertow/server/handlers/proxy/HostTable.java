package io.undertow.server.handlers.proxy;

import io.undertow.UndertowMessages;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.PathMatcher;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class HostTable<H> {
   private final Map<H, Set<Target>> hosts = new CopyOnWriteMap();
   private final Map<String, PathMatcher<Set<H>>> targets = new CopyOnWriteMap();

   public synchronized HostTable addHost(H host) {
      if (this.hosts.containsKey(host)) {
         throw UndertowMessages.MESSAGES.hostAlreadyRegistered(host);
      } else {
         this.hosts.put(host, new CopyOnWriteArraySet());
         return this;
      }
   }

   public synchronized HostTable removeHost(H host) {
      Set<Target> targets = (Set)this.hosts.remove(host);
      Iterator var3 = targets.iterator();

      while(var3.hasNext()) {
         Target target = (Target)var3.next();
         this.removeRoute(host, target.virtualHost, target.contextPath);
      }

      return this;
   }

   public synchronized HostTable addRoute(H host, String virtualHost, String contextPath) {
      Set<Target> hostData = (Set)this.hosts.get(host);
      if (hostData == null) {
         throw UndertowMessages.MESSAGES.hostHasNotBeenRegistered(host);
      } else {
         hostData.add(new Target(virtualHost, contextPath));
         PathMatcher<Set<H>> paths = (PathMatcher)this.targets.get(virtualHost);
         if (paths == null) {
            paths = new PathMatcher();
            this.targets.put(virtualHost, paths);
         }

         Set<H> hostSet = (Set)paths.getPrefixPath(contextPath);
         if (hostSet == null) {
            hostSet = new CopyOnWriteArraySet();
            paths.addPrefixPath(contextPath, hostSet);
         }

         ((Set)hostSet).add(host);
         return this;
      }
   }

   public synchronized HostTable removeRoute(H host, String virtualHost, String contextPath) {
      Set<Target> hostData = (Set)this.hosts.get(host);
      if (hostData != null) {
         hostData.remove(new Target(virtualHost, contextPath));
      }

      PathMatcher<Set<H>> paths = (PathMatcher)this.targets.get(virtualHost);
      if (paths == null) {
         return this;
      } else {
         Set<H> hostSet = (Set)paths.getPrefixPath(contextPath);
         if (hostSet == null) {
            return this;
         } else {
            hostSet.remove(host);
            if (hostSet.isEmpty()) {
               paths.removePrefixPath(contextPath);
            }

            return this;
         }
      }
   }

   public Set<H> getHostsForTarget(String hostName, String path) {
      PathMatcher<Set<H>> matcher = (PathMatcher)this.targets.get(hostName);
      return matcher == null ? null : (Set)matcher.match(path).getValue();
   }

   private static final class Target {
      final String virtualHost;
      final String contextPath;

      private Target(String virtualHost, String contextPath) {
         this.virtualHost = virtualHost;
         this.contextPath = contextPath;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Target target = (Target)o;
            if (this.contextPath != null) {
               if (!this.contextPath.equals(target.contextPath)) {
                  return false;
               }
            } else if (target.contextPath != null) {
               return false;
            }

            if (this.virtualHost != null) {
               if (this.virtualHost.equals(target.virtualHost)) {
                  return true;
               }
            } else if (target.virtualHost == null) {
               return true;
            }

            return false;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.virtualHost != null ? this.virtualHost.hashCode() : 0;
         result = 31 * result + (this.contextPath != null ? this.contextPath.hashCode() : 0);
         return result;
      }

      // $FF: synthetic method
      Target(String x0, String x1, Object x2) {
         this(x0, x1);
      }
   }
}
