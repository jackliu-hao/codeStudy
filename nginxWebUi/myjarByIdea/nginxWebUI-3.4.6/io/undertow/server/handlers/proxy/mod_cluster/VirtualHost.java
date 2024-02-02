package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowMessages;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.PathMatcher;
import io.undertow.util.URLUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

public class VirtualHost {
   private static final String STRING_PATH_SEPARATOR = "/";
   private final HostEntry defaultHandler = new HostEntry("/");
   private final ConcurrentMap<String, HostEntry> contexts = new CopyOnWriteMap();
   private volatile int[] lengths = new int[0];

   protected VirtualHost() {
   }

   PathMatcher.PathMatch<HostEntry> match(String path) {
      int length = path.length();
      int[] lengths = this.lengths;

      for(int i = 0; i < lengths.length; ++i) {
         int pathLength = lengths[i];
         if (pathLength == length) {
            HostEntry next = (HostEntry)this.contexts.get(path);
            if (next != null) {
               return new PathMatcher.PathMatch(path, "", next);
            }
         } else if (pathLength < length) {
            char c = path.charAt(pathLength);
            if (c == '/') {
               String part = path.substring(0, pathLength);
               HostEntry next = (HostEntry)this.contexts.get(part);
               if (next != null) {
                  return new PathMatcher.PathMatch(part, path.substring(pathLength), next);
               }
            }
         }
      }

      if (this.defaultHandler.contexts.isEmpty()) {
         return new PathMatcher.PathMatch("", path, (Object)null);
      } else {
         return new PathMatcher.PathMatch("", path, this.defaultHandler);
      }
   }

   public synchronized void registerContext(String path, String jvmRoute, Context context) {
      if (path.isEmpty()) {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      } else {
         String normalizedPath = URLUtils.normalizeSlashes(path);
         if ("/".equals(normalizedPath)) {
            this.defaultHandler.contexts.put(jvmRoute, context);
         } else {
            boolean rebuild = false;
            HostEntry hostEntry = (HostEntry)this.contexts.get(normalizedPath);
            if (hostEntry == null) {
               rebuild = true;
               hostEntry = new HostEntry(normalizedPath);
               this.contexts.put(normalizedPath, hostEntry);
            }

            assert !hostEntry.contexts.containsKey(jvmRoute);

            hostEntry.contexts.put(jvmRoute, context);
            if (rebuild) {
               this.buildLengths();
            }

         }
      }
   }

   public synchronized void removeContext(String path, String jvmRoute, Context context) {
      if (path != null && !path.isEmpty()) {
         String normalizedPath = URLUtils.normalizeSlashes(path);
         if ("/".equals(normalizedPath)) {
            this.defaultHandler.contexts.remove(jvmRoute, context);
         }

         HostEntry hostEntry = (HostEntry)this.contexts.get(normalizedPath);
         if (hostEntry != null && hostEntry.contexts.remove(jvmRoute, context) && hostEntry.contexts.isEmpty()) {
            this.contexts.remove(normalizedPath);
            this.buildLengths();
         }

      } else {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      }
   }

   boolean isEmpty() {
      return this.contexts.isEmpty() && this.defaultHandler.contexts.isEmpty();
   }

   private void buildLengths() {
      Set<Integer> lengths = new TreeSet(new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            return -o1.compareTo(o2);
         }
      });
      Iterator var2 = this.contexts.keySet().iterator();

      while(var2.hasNext()) {
         String p = (String)var2.next();
         lengths.add(p.length());
      }

      int[] lengthArray = new int[lengths.size()];
      int pos = 0;

      int i;
      for(Iterator var4 = lengths.iterator(); var4.hasNext(); lengthArray[pos++] = i) {
         i = (Integer)var4.next();
      }

      this.lengths = lengthArray;
   }

   static class HostEntry {
      private final ConcurrentMap<String, Context> contexts = new CopyOnWriteMap();
      private final String contextPath;

      HostEntry(String contextPath) {
         this.contextPath = contextPath;
      }

      protected String getContextPath() {
         return this.contextPath;
      }

      protected Context getContextForNode(String jvmRoute) {
         return (Context)this.contexts.get(jvmRoute);
      }

      protected Collection<String> getNodes() {
         return Collections.unmodifiableCollection(this.contexts.keySet());
      }

      protected Collection<Context> getContexts() {
         return Collections.unmodifiableCollection(this.contexts.values());
      }
   }
}
