package io.undertow.util;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

public class PathMatcher<T> {
   private static final String STRING_PATH_SEPARATOR = "/";
   private volatile T defaultHandler;
   private final SubstringMap<T> paths = new SubstringMap();
   private final ConcurrentMap<String, T> exactPathMatches = new CopyOnWriteMap();
   private volatile int[] lengths = new int[0];

   public PathMatcher(T defaultHandler) {
      this.defaultHandler = defaultHandler;
   }

   public PathMatcher() {
   }

   public Set<String> getExactPathMatchesSet() {
      return Collections.unmodifiableSet(this.exactPathMatches.keySet());
   }

   public Set<String> getPathMatchesSet() {
      return Collections.unmodifiableSet(this.paths.toMap().keySet());
   }

   public PathMatch<T> match(String path) {
      if (!this.exactPathMatches.isEmpty()) {
         T match = this.getExactPath(path);
         if (match != null) {
            UndertowLogger.REQUEST_LOGGER.debugf("Matched exact path %s", path);
            return new PathMatch(path, "", match);
         }
      }

      int length = path.length();
      int[] lengths = this.lengths;

      for(int i = 0; i < lengths.length; ++i) {
         int pathLength = lengths[i];
         if (pathLength == length) {
            SubstringMap.SubstringMatch<T> next = this.paths.get(path, length);
            if (next != null) {
               UndertowLogger.REQUEST_LOGGER.debugf("Matched prefix path %s for path %s", next.getKey(), path);
               return new PathMatch(path, "", next.getValue());
            }
         } else if (pathLength < length) {
            char c = path.charAt(pathLength);
            if (c == '/') {
               SubstringMap.SubstringMatch<T> next = this.paths.get(path, pathLength);
               if (next != null) {
                  UndertowLogger.REQUEST_LOGGER.debugf("Matched prefix path %s for path %s", next.getKey(), path);
                  return new PathMatch(next.getKey(), path.substring(pathLength), next.getValue());
               }
            }
         }
      }

      UndertowLogger.REQUEST_LOGGER.debugf("Matched default handler path %s", path);
      return new PathMatch("", path, this.defaultHandler);
   }

   public synchronized PathMatcher addPrefixPath(String path, T handler) {
      if (path.isEmpty()) {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      } else {
         String normalizedPath = URLUtils.normalizeSlashes(path);
         if ("/".equals(normalizedPath)) {
            this.defaultHandler = handler;
            return this;
         } else {
            this.paths.put(normalizedPath, handler);
            this.buildLengths();
            return this;
         }
      }
   }

   public synchronized PathMatcher addExactPath(String path, T handler) {
      if (path.isEmpty()) {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      } else {
         this.exactPathMatches.put(URLUtils.normalizeSlashes(path), handler);
         return this;
      }
   }

   public T getExactPath(String path) {
      return this.exactPathMatches.get(URLUtils.normalizeSlashes(path));
   }

   public T getPrefixPath(String path) {
      String normalizedPath = URLUtils.normalizeSlashes(path);
      SubstringMap.SubstringMatch<T> match = this.paths.get(normalizedPath);
      if ("/".equals(normalizedPath) && match == null) {
         return this.defaultHandler;
      } else {
         return match == null ? null : match.getValue();
      }
   }

   private void buildLengths() {
      Set<Integer> lengths = new TreeSet(new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            return -o1.compareTo(o2);
         }
      });
      Iterator var2 = this.paths.keys().iterator();

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

   /** @deprecated */
   @Deprecated
   public synchronized PathMatcher removePath(String path) {
      return this.removePrefixPath(path);
   }

   public synchronized PathMatcher removePrefixPath(String path) {
      if (path != null && !path.isEmpty()) {
         String normalizedPath = URLUtils.normalizeSlashes(path);
         if ("/".equals(normalizedPath)) {
            this.defaultHandler = null;
            return this;
         } else {
            this.paths.remove(normalizedPath);
            this.buildLengths();
            return this;
         }
      } else {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      }
   }

   public synchronized PathMatcher removeExactPath(String path) {
      if (path != null && !path.isEmpty()) {
         this.exactPathMatches.remove(URLUtils.normalizeSlashes(path));
         return this;
      } else {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      }
   }

   public synchronized PathMatcher clearPaths() {
      this.paths.clear();
      this.exactPathMatches.clear();
      this.lengths = new int[0];
      this.defaultHandler = null;
      return this;
   }

   public Map<String, T> getPaths() {
      return this.paths.toMap();
   }

   public static final class PathMatch<T> {
      private final String matched;
      private final String remaining;
      private final T value;

      public PathMatch(String matched, String remaining, T value) {
         this.matched = matched;
         this.remaining = remaining;
         this.value = value;
      }

      public String getRemaining() {
         return this.remaining;
      }

      public String getMatched() {
         return this.matched;
      }

      public T getValue() {
         return this.value;
      }
   }
}
